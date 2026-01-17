package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.GuardianOfTheLawPower;
import thePenance.util.CardStats;

public class GuardianOfTheLaw extends BaseCard {

    public static final String ID = makeID("GuardianOfTheLaw");

    private static final int COST = 2;
    private static final int UPGRADE_COST = 1;
    private static final int MAGIC = 3;

    public GuardianOfTheLaw() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.RARE,
                CardTarget.SELF,
                COST
        ));

        setMagic(MAGIC);

        // 设置升级后的费用
        setCostUpgrade(UPGRADE_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new GuardianOfTheLawPower(p, magicNumber), magicNumber));
    }
}