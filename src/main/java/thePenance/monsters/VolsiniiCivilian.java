package thePenance.monsters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod;
import thePenance.events.VolsiniiCourtEvent;

public class VolsiniiCivilian extends AbstractMonster {
    public static final String ID = PenanceMod.makeID("VolsiniiCivilian");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    // 资源路径常量（使用正斜杠 /）
    private static final String ATLAS_PATH = "thePenance/images/enemies/VolsiniiCivilian/enemy_3004_speople.atlas";
    private static final String JSON_PATH = "thePenance/images/enemies/VolsiniiCivilian/enemy_3004_speople.json";
    private static final float SCALE = 1.5F; // 根据需要调整大小，通常是 1.0F 到 1.5F

    private boolean escaped = false;

    public VolsiniiCivilian(float x, float y) {
        // 1. 修改 super 调用：将 imgUrl 参数设为 null
        super(monsterStrings.NAME, ID, 30, 0.0F, 0.0F, 100.0F, 200.0F, null, x, y);

        this.type = EnemyType.NORMAL;

        // 2. 加载 Spine 动画
        this.loadAnimation(ATLAS_PATH, JSON_PATH, SCALE);

        // 3. 设置初始动画
        // 注意："Idle" 是动画名称，必须与你的 .json 文件内的名称完全一致！
        // 如果游戏崩溃，请检查 json 文件中 "animations" 下的键名（可能是 "idle", "animation", "stand" 等）
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);

        this.flipHorizontal = false;
    }

    @Override
    public void update() {
        super.update();

        // 如果已经死亡、正在逃跑或已经逃跑，就不检测了
        if (this.isDying || this.isEscaping || this.escaped) {
            return;
        }

        // 检测场上是否还有其他活着的敌人
        boolean allEnemiesDead = true;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m != this && !m.isDeadOrEscaped()) {
                allEnemiesDead = false;
                break;
            }
        }

        // 如果其他人都死了，市民逃跑，战斗胜利
        if (allEnemiesDead) {
            this.escaped = true;
            // 喊一句“谢谢！”
            AbstractDungeon.actionManager.addToBottom(new TalkAction(this, monsterStrings.DIALOG[2], 1.0F, 2.0F));
            // 执行逃跑动作
            AbstractDungeon.actionManager.addToBottom(new EscapeAction(this));
        }
    }

    @Override
    public void renderReticle(SpriteBatch sb) {
        // 隐藏红色选中框
    }

    @Override
    public void takeTurn() {
        if (!this.escaped) {
            AbstractDungeon.actionManager.addToBottom(new TalkAction(this, monsterStrings.DIALOG[0], 1.0F, 2.0F));
        }
    }

    @Override
    protected void getMove(int num) {
        this.setMove((byte)0, Intent.UNKNOWN);
    }

    @Override
    public void damage(DamageInfo info) {
        // 这里的代码只是最后一道防线，真正的禁止选中需要靠下面的 Patch
        if (info.owner != null && info.owner.isPlayer) {
            info.output = 0;
        }
        super.damage(info);
    }

    @Override
    public void die() {
        super.die();
        VolsiniiCourtEvent.civilianDied = true;
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, monsterStrings.DIALOG[1], 1.0F, 2.0F));

        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m != null && !m.isDeadOrEscaped()) {
                m.applyPowers();
            }
        }
    }
}