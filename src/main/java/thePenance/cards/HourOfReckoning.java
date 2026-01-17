package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.patches.HpLostTracker; // 引用 Tracker
import thePenance.util.CardStats;

public class HourOfReckoning extends BaseCard {
    public static final String ID = makeID("HourOfReckoning");
    private static final int COST = 2;
    private static final int DAMAGE = 20;
    private static final int UPG_DAMAGE = 5; // 20->25

    public HourOfReckoning() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
        setEthereal(true);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBase = this.baseDamage;
        // 加上损失的血量
        this.baseDamage += HpLostTracker.hpLostThisCombat;

        super.calculateCardDamage(mo);

        this.baseDamage = realBase;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void applyPowers() {
        int realBase = this.baseDamage;
        this.baseDamage += HpLostTracker.hpLostThisCombat;
        super.applyPowers();
        this.baseDamage = realBase;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateCardDamage(m); // 确保数值最新
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SMASH));
    }
}