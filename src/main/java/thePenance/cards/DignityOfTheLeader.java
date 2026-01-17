package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import thePenance.PenanceMod;
import thePenance.powers.CeasefirePower;
import thePenance.util.CardStats;

public class DignityOfTheLeader extends BaseCard {

    public static final String ID = makeID("DignityOfTheLeader");
    private static final int COST = 1;
    private static final int ENERGY_GAIN = 2;
    private static final int UPG_ENERGY_GAIN = 1; // 2->3

    public DignityOfTheLeader() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.ALL,
                COST
        ));
        setMagic(ENERGY_GAIN, UPG_ENERGY_GAIN);
        setExhaust(true);

        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new CeasefirePower(p, 1), 1));

        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(mo, p, new CeasefirePower(mo, 1), 1));
            }
        }

        // 3. 获得能量
        addToBot(new GainEnergyAction(magicNumber));
    }

    @Override
    public void triggerWhenDrawn() {
        addToBot(new NewQueueCardAction(this, true, false, true));
    }
}