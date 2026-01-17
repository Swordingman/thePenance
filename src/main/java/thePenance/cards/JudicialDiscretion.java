package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class JudicialDiscretion extends BaseCard {
    public static final String ID = makeID("JudicialDiscretion");
    private static final int COST = 0;
    private static final int DRAW = 1;
    private static final int UPG_DRAW = 1; // 1->2
    private static final int BARRIER_COST = 5;

    public JudicialDiscretion() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON, // 普通
                CardTarget.SELF,
                COST
        ));
        setMagic(DRAW, UPG_DRAW);
        setCustomVar("BarrierCost", BARRIER_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 失去5屏障 (ReducePowerAction 会自动处理 amount > current 的情况，扣到0为止)
        if (p.hasPower(BarrierPower.POWER_ID)) {
            addToBot(new ReducePowerAction(p, p, BarrierPower.POWER_ID, customVar("BarrierCost")));
        }

        // 获得1能量
        addToBot(new GainEnergyAction(1));

        // 抽牌
        addToBot(new DrawCardAction(p, magicNumber));
    }
}