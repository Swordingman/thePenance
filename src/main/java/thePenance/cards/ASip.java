package thePenance.cards;

import basemod.cardmods.EtherealMod;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.GameDictionary; // 必须导入
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class ASip extends BaseCard {

    public static final String ID = makeID("ASip");

    private static final int COST = 1;
    private static final int COST_AMOUNT = 3;
    private static final int UPG_COST_AMOUNT = -1;
    private static final int STRENGTH_AMT = 2;
    private static final int COST_INCREMENT = 2;

    // 🌟 核心修复：记录这张卡在“未升级状态”下的基础消耗数值
    private int unupgradedBaseCost;

    public ASip() {
        this(COST_AMOUNT);
    }

    public ASip(int inheritedUnupgradedCost) {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        // 保存未升级时的数值（比如 3, 5, 7...）
        this.unupgradedBaseCost = inheritedUnupgradedCost;

        // setMagic 会把 baseMagicNumber 设为未升级的数值。
        // 等到后续调用 upgrade() 时，系统会自动从这个数值上减去 1
        setMagic(this.unupgradedBaseCost, UPG_COST_AMOUNT);
        setExhaust(true);
    }

    @Override
    public AbstractCard makeCopy() {
        // 复制时永远传递它“未升级时的基础值”
        return new ASip(this.unupgradedBaseCost);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        final int costToPaySnapshot = this.magicNumber;

        // 🌟 核心修复：使用 unupgradedBaseCost 来计算下一次的累积值
        // 如果当前是第一张卡，这里算出来就是 3 + 2 = 5
        final int nextUnupgradedCost = this.unupgradedBaseCost + COST_INCREMENT;

        // ======== 扣除屏障 / 生命 (保持上一次的修复逻辑) ========
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int barrierToLose = 0;
                if (p.hasPower(BarrierPower.POWER_ID)) {
                    AbstractPower barrier = p.getPower(BarrierPower.POWER_ID);
                    if (barrier.amount > 0) {
                        barrierToLose = Math.min(barrier.amount, costToPaySnapshot);
                        addToTop(new ReducePowerAction(p, p, BarrierPower.POWER_ID, barrierToLose));
                    }
                }

                int hpToLose = costToPaySnapshot - barrierToLose;
                if (hpToLose > 0) {
                    addToTop(new LoseHPAction(p, p, hpToLose));
                }

                this.isDone = true;
            }
        });

        // ======== 加力量 ========
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, STRENGTH_AMT), STRENGTH_AMT));

        // ======== 生成复制卡 ========
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 用下一次的“未升级基础值”创建新卡 (例如传进去 5)
                ASip copy = new ASip(nextUnupgradedCost);

                // 如果原卡是升级过的，现在再赋予它升级属性
                // 这时 copy.upgrade() 会将它减 1，变成 4。
                // 完美实现了： 2 -> 4 -> 6 的累积！
                if (ASip.this.upgraded) {
                    copy.upgrade();
                }

                CardModifierManager.addModifier(copy, new EtherealMod());

                copy.applyPowers();
                copy.initializeDescription();

                addToBot(new MakeTempCardInHandAction(copy));
                this.isDone = true;
            }
        });
    }
}