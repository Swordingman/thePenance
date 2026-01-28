package thePenance.monsters;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower; // 必须导入这个
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.PenanceMod;

import java.util.Comparator;

public class MafiaGodfather extends AbstractMonster {
    public static final String ID = PenanceMod.makeID("MafiaGodfather");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH = "thePenance/images/enemies/MafiaGodfather/enemy_1282_sgelder_2.atlas";
    private static final String JSON_PATH = "thePenance/images/enemies/MafiaGodfather/enemy_1282_sgelder_2.json";
    private static final float SCALE = 1.5F; // 根据需要调整大小，通常是 1.0F 到 1.5F

    private static final int HP = 120;
    private static final int STR_AMT = 2;
    private static final int BLOCK_AMT = 15;

    public MafiaGodfather(float x, float y) {
        super(monsterStrings.NAME, ID, HP, 0.0F, 0.0F, 220.0F, 300.0F, null, x, y);

        this.type = EnemyType.NORMAL;

        // 2. 加载 Spine 动画
        this.loadAnimation(ATLAS_PATH, JSON_PATH, SCALE);

        // 3. 设置初始动画
        // 注意："Idle" 是动画名称，必须与你的 .json 文件内的名称完全一致！
        // 如果游戏崩溃，请检查 json 文件中 "animations" 下的键名（可能是 "idle", "animation", "stand" 等）
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);

        this.flipHorizontal = true;
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, "清理干净。", 1.0F, 2.0F));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case 1: // 召唤
                // 左护法
                AbstractMonster minion1 = new FamilyCleaner(-350.0F, 0.0F);
                addToBot(new SpawnMonsterAction(minion1, true));
                // 【核心修复】必须手动给召唤出来的怪加 MinionPower，否则老大死了战斗不会结束
                addToBot(new ApplyPowerAction(minion1, this, new MinionPower(minion1)));

                // 右护法
                AbstractMonster minion2 = new FamilyCleaner(-100.0F, 0.0F);
                addToBot(new SpawnMonsterAction(minion2, true));
                // 【核心修复】同上
                addToBot(new ApplyPowerAction(minion2, this, new MinionPower(minion2)));

                // 重新排序怪物位置，防止遮挡
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.getMonsters().monsters.sort(new Comparator<AbstractMonster>() {
                            @Override
                            public int compare(AbstractMonster m1, AbstractMonster m2) {
                                return Float.compare(m1.drawX, m2.drawX);
                            }
                        });
                        this.isDone = true;
                    }
                });
                break;

            case 2: // 全体加力
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDeadOrEscaped()) {
                        addToBot(new ApplyPowerAction(m, this, new StrengthPower(m, STR_AMT), STR_AMT));
                    }
                }
                break;

            case 3: // 全体格挡
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDeadOrEscaped()) {
                        addToBot(new GainBlockAction(m, this, BLOCK_AMT));
                    }
                }
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        int livingMinions = 0;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.id.equals(this.id) && !m.isDeadOrEscaped()) {
                livingMinions++;
            }
        }

        if (livingMinions == 0) {
            this.setMove((byte)1, Intent.UNKNOWN);
        } else {
            if (this.lastMove((byte)2)) {
                this.setMove((byte)3, Intent.DEFEND_BUFF);
            } else {
                this.setMove((byte)2, Intent.BUFF);
            }
        }
    }

    @Override
    public void die() {
        super.die();
        // 遍历当前房间内的所有怪物
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            // 只要怪物不是已经死了、不是正在死、且不是教父自己（即它是召唤出的 FamilyCleaner）
            if (!m.isDead && !m.isDying && !m.id.equals(this.id)) {
                // 强制让它们逃跑
                // 逃跑动作会自动触发“爪牙逃离”的视觉效果，并从逻辑上移除怪物
                AbstractDungeon.actionManager.addToBottom(new EscapeAction(m));
            }
        }
    }
}