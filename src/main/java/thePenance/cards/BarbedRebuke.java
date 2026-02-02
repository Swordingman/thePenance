package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

public class BarbedRebuke extends BaseCard {

    public static final String ID = makeID("BarbedRebuke");

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3; // 6->9

    public BarbedRebuke() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 基础伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));

        // 获取荆棘层数
        int thorns = 0;
        if (p.hasPower(ThornAuraPower.POWER_ID)) {
            thorns = p.getPower(ThornAuraPower.POWER_ID).amount;
        }

        if (thorns > 0) {
            // 2. 触发荆棘伤害 (荆棘通常是 THORNS 类型，无视格挡)
            addToBot(new DamageAction(m, new DamageInfo(p, thorns, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.POISON));

            // 3. 如果意图是攻击，额外触发一半
            if (m.getIntentBaseDmg() >= 0) {
                int extraDmg = thorns / 2;
                if (extraDmg > 0) {
                    addToBot(new DamageAction(m, new DamageInfo(p, extraDmg, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.POISON));
                }
            }
        }
    }
}