package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.powers.CeasefirePower;
import thePenance.util.CardStats;

public class LastStand extends BaseCard {

    public static final String ID = makeID("LastStand");

    private static final int COST = 2;
    private static final int BARRIER = 30;

    // 止戈层数：基础3，升级后-1(变为2)
    private static final int CEASEFIRE = 3;
    private static final int UPG_CEASEFIRE = -1;

    public LastStand() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setExhaust(true);

        // 使用自定义变量来存屏障，方便看
        setCustomVar("Barrier", BARRIER);

        // MagicNumber 存止戈层数
        setMagic(CEASEFIRE, UPG_CEASEFIRE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得屏障
        int barrier = customVar("Barrier");
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, barrier), barrier));

        // 获得止戈
        addToBot(new ApplyPowerAction(p, p, new CeasefirePower(p, magicNumber), magicNumber));
    }
}