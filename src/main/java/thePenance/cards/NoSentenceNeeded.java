package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class NoSentenceNeeded extends BaseCard {
    public static final String ID = makeID("NoSentenceNeeded");
    private static final int COST = 1;
    private static final int DAMAGE = 50;
    private static final int UPG_DAMAGE = 10; // 50->60

    public NoSentenceNeeded() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.RARE,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
        setExhaust(true);
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if (!canUse) return false;

        // 检查屏障
        if (p.hasPower(BarrierPower.POWER_ID)) {
            int barrierAmt = p.getPower(BarrierPower.POWER_ID).amount;
            if (barrierAmt > 0) {
                this.cantUseMessage = "我有屏障时不能打出此牌。";
                return false;
            }
        }

        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SMASH));
    }
}