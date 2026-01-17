package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.CodeOfRevengePower;
import thePenance.util.CardStats;

public class CodeOfRevenge extends BaseCard {

    public static final String ID = makeID("CodeOfRevenge");

    private static final int COST = 2;
    private static final int UPGRADED_COST = 1;
    private static final int BARRIER_GAIN = 3;

    public CodeOfRevenge() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.RARE,
                CardTarget.SELF,
                COST
        ));

        // 设置魔法数字（屏障获取量）
        setMagic(BARRIER_GAIN);

        // 设置费用升级：升级后变成 1 费
        setCostUpgrade(UPGRADED_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new CodeOfRevengePower(p, magicNumber), magicNumber));
    }
}