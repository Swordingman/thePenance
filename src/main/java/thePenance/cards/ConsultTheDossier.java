package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.util.CardStats;

import java.util.ArrayList;

public class ConsultTheDossier extends BaseCard {

    public static final String ID = makeID("ConsultTheDossier");

    private static final int COST = 1;
    private static final int DRAW_AMT = 2;
    private static final int UPG_DRAW_AMT = 1; // 升级后抽 3
    private static final int JUDGE_AMT = 3;    // 裁决固定 3

    public ConsultTheDossier() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        // MagicNumber = 抽牌数
        setMagic(DRAW_AMT, UPG_DRAW_AMT);

        // 自定义变量 Judge = 获得的裁决数
        setCustomVar("Judge", JUDGE_AMT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 调用自定义动作
        addToBot(new ConsultAction(magicNumber, customVar("Judge")));
    }

    // --- 内部 Action 类：处理抽牌检测逻辑 ---
    // 将其写在卡牌内部或者单独一个文件都可以，这里为了方便直接写成静态内部类
    public static class ConsultAction extends AbstractGameAction {
        private final int drawAmount;
        private final int judgeAmount;

        public ConsultAction(int drawAmount, int judgeAmount) {
            this.drawAmount = drawAmount;
            this.judgeAmount = judgeAmount;
        }

        @Override
        public void update() {
            AbstractPlayer p = AbstractDungeon.player;

            // 1. 记录当前手牌的快照（引用列表）
            ArrayList<AbstractCard> handSnapshot = new ArrayList<>(p.hand.group);

            // 2. 将“检查逻辑”添加到队列顶部（后执行）
            // 因为 ActionManager 是栈结构（先进后出），我们先加“检查”，后加“抽牌”，
            // 实际执行顺序就是：抽牌 -> 检查。
            addToTop(new AbstractGameAction() {
                @Override
                public void update() {
                    boolean powerDrawn = false;

                    // 遍历现在的每一张手牌
                    for (AbstractCard c : p.hand.group) {
                        // 如果这张牌不在刚才的快照里，说明是新抽上来的
                        if (!handSnapshot.contains(c)) {
                            if (c.type == CardType.POWER) {
                                powerDrawn = true;
                                break; // 只要有一张能力牌就触发
                            }
                        }
                    }

                    if (powerDrawn) {
                        addToTop(new ApplyPowerAction(p, p, new JudgementPower(p, judgeAmount), judgeAmount));
                    }

                    this.isDone = true;
                }
            });

            // 3. 将“抽牌逻辑”添加到队列顶部（先执行）
            addToTop(new DrawCardAction(drawAmount));

            // 本 Action 任务完成
            this.isDone = true;
        }
    }
}