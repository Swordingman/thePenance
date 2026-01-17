package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.GlowOfSufferingPower;
import thePenance.util.CardStats;

public class GlowOfSuffering extends BaseCard {
    public static final String ID = makeID("GlowOfSuffering");
    private static final int COST = 1;

    private static final int THORNS = 1;
    private static final int UPG_THORNS = 1; // 1->2

    // 屏障倍率
    private static final int MULTIPLIER = 1;
    private static final int UPG_MULTIPLIER = 1; // 1->2 (即翻倍)

    public GlowOfSuffering() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        setMagic(THORNS, UPG_THORNS);
        setCustomVar("Multiplier", MULTIPLIER, UPG_MULTIPLIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int multi = customVar("Multiplier");
        if (p.hasPower(GlowOfSufferingPower.POWER_ID)) {
            GlowOfSufferingPower power = (GlowOfSufferingPower) p.getPower(GlowOfSufferingPower.POWER_ID);
            power.stackPower(magicNumber, multi);
        } else {
            addToBot(new ApplyPowerAction(p, p, new GlowOfSufferingPower(p, magicNumber, multi), magicNumber));
        }
    }
}