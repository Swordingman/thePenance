package thePenance.monsters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    private boolean escaped = false; // 防止重复触发逃跑

    public VolsiniiCivilian(float x, float y) {
        super(monsterStrings.NAME, ID, 30, 0.0F, 0.0F, 100.0F, 200.0F, "thePenance/images/character/animation/object1.png", x, y);
        this.type = EnemyType.NORMAL;
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