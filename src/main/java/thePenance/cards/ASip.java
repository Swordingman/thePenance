package thePenance.cards;

import basemod.cardmods.EtherealMod;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
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
        int currentMagicValue = this.magicNumber;

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int costToPay = currentMagicValue;
                int barrierToLose = 0;
                if (p.hasPower(BarrierPower.POWER_ID)) {
                    AbstractPower barrier = p.getPower(BarrierPower.POWER_ID);
                    if (barrier.amount > 0) {
                        barrierToLose = Math.min(barrier.amount, costToPay);
                        barrier.amount -= barrierToLose;
                        barrier.updateDescription();
                        barrier.flash();
                    }
                }
                int hpToLose = costToPay - barrierToLose;
                if (hpToLose > 0) {
                    addToTop(new LoseHPAction(p, p, hpToLose));
                }
                this.isDone = true;
            }
        });

        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, STRENGTH_AMT), STRENGTH_AMT));

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractCard c = ASip.this.makeStatEquivalentCopy();

                CardModifierManager.addModifier(c, new EtherealMod());
                c.baseMagicNumber += COST_INCREMENT;
                c.magicNumber += COST_INCREMENT;
                c.initializeDescription();

                addToBot(new MakeTempCardInHandAction(c));

                this.isDone = true;
            }
        });
    }
}