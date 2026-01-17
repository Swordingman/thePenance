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

public class Quell extends BaseCard {
    // ID 修改为 RiotSuppression
    public static final String ID = makeID("Quell");

    private static final int COST = 1;
    private static final int DAMAGE = 8;
    private static final int UPG_DAMAGE = 3; // 8->11
    private static final int WEAK = 1;
    private static final int UPG_WEAK = 1; // 1->2

    public Quell() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(WEAK, UPG_WEAK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 造成伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        // 检查是否有屏障
        if (p.hasPower(BarrierPower.POWER_ID) && p.getPower(BarrierPower.POWER_ID).amount > 0) {
            // 给予虚弱 (镇压效果)
            addToBot(new ApplyPowerAction(m, p, new WeakPower(m, magicNumber, false), magicNumber));
        }
    }
}