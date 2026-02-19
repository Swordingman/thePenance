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

    public ASip() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        setMagic(COST_AMOUNT, UPG_COST_AMOUNT);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        // ======== 快照当前卡状态，避免后续被 reset / 修改 ========
        final int costToPaySnapshot = this.magicNumber;
        final int newBaseMagic = this.baseMagicNumber + COST_INCREMENT;
        final boolean upgradedSnapshot = this.upgraded;
        final int timesUpgradedSnapshot = this.timesUpgraded;

        // ======== 扣除屏障 / 生命 ========
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
        addToBot(new ApplyPowerAction(p, p,
                new StrengthPower(p, STRENGTH_AMT),
                STRENGTH_AMT));

        // ======== 生成复制卡（完全防御性构造） ========
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {

                // 永远创建全新实例
                AbstractCard c = new ASip();

                // 如果原卡是升级过的，恢复升级状态
                if (upgradedSnapshot) {
                    for (int i = 0; i < timesUpgradedSnapshot; i++) {
                        c.upgrade();
                    }
                }

                // 手动设置新的 magic 数值
                c.baseMagicNumber = newBaseMagic;
                c.magicNumber = newBaseMagic;
                c.isMagicNumberModified = false;

                // 添加 Ethereal
                CardModifierManager.addModifier(c, new EtherealMod());

                // 重新应用 powers，确保显示正确
                c.applyPowers();
                c.initializeDescription();

                addToBot(new MakeTempCardInHandAction(c));

                this.isDone = true;
            }
        });
    }

}