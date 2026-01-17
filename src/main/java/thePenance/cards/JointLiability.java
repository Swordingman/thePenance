package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.util.CardStats;

public class JointLiability extends BaseCard {

    public static final String ID = makeID("JointLiability");

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3; // 6 -> 9

    public JointLiability() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.UNCOMMON,
                CardTarget.ALL_ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPG_DAMAGE);
        this.isMultiDamage = true;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        // 加上裁决的伤害
        int judge = 0;
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasPower(JudgementPower.POWER_ID)) {
            judge = p.getPower(JudgementPower.POWER_ID).amount;
        }

        int realBaseDamage = this.baseDamage;
        this.baseDamage += judge;

        super.calculateCardDamage(mo);

        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    // applyPowers 同理，用于显示 AOE 数值
    @Override
    public void applyPowers() {
        int judge = 0;
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasPower(JudgementPower.POWER_ID)) {
            judge = p.getPower(JudgementPower.POWER_ID).amount;
        }

        int realBaseDamage = this.baseDamage;
        this.baseDamage += judge;

        super.applyPowers();

        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 再次计算以确保数值正确（因为是 AOE，Action 会用 multiDamage 数组）
        applyPowers();
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, damageTypeForTurn, AbstractGameAction.AttackEffect.FIRE));
    }
}