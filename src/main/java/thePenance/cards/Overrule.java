package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class Overrule extends BaseCard {
    public static final String ID = makeID("Overrule");
    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3; // 6->9
    private static final int BARRIER_REWARD = 5;

    public Overrule() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
        setCustomVar("Barrier", BARRIER_REWARD);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        // 2. 判定是否已经拥有虚弱 (在给予新虚弱之前判定)
        boolean alreadyWeak = m.hasPower(WeakPower.POWER_ID);

        // 3. 给予1层虚弱
        addToBot(new ApplyPowerAction(m, p, new WeakPower(m, 1, false), 1));

        // 4. 如果判定满足，获得屏障
        if (alreadyWeak) {
            int b = customVar("Barrier");
            addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, b), b));
        }
    }
}