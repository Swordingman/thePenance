package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class PendingJudgment extends BaseCard {
    public static final String ID = makeID("PendingJudgment");
    private static final int COST = 1;
    private static final int BARRIER = 8;
    private static final int UPG_BARRIER = 3; // 8->11

    public PendingJudgment() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        setMagic(BARRIER, UPG_BARRIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int count = 0;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped() && mo.getIntentBaseDmg() >= 0) {
                count++;
            }
        }

        if (count > 0) {
            int totalBarrier = count * magicNumber;
            addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, totalBarrier), totalBarrier));
        }
    }
}