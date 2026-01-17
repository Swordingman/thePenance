package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.character.Penance;
import thePenance.powers.CeasefirePower;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

public class GuiltyAsCharged extends BaseCard {
    public static final String ID = makeID("GuiltyAsCharged");
    private static final int COST = 2;
    private static final int DAMAGE = 15;
    private static final int UPG_DAMAGE = 5; // 15->20

    public GuiltyAsCharged() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.RARE,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 判定是否触发双倍效果 (力量 >= 10)
        int repeatTimes = 1;
        if (p.hasPower(StrengthPower.POWER_ID) && p.getPower(StrengthPower.POWER_ID).amount >= 10) {
            repeatTimes = 2;
        }

        // 1. 击碎护甲 (只做一次通常就够了，但为了严谨放在循环外)
        addToBot(new RemoveAllBlockAction(m, p));

        for (int i = 0; i < repeatTimes; i++) {
            // 2. 基础伤害
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

            // 3. 裁决 >= 10 额外伤害
            if (p.hasPower(JudgementPower.POWER_ID)) {
                int judge = p.getPower(JudgementPower.POWER_ID).amount;
                if (judge >= 10) {
                    addToBot(new DamageAction(m, new DamageInfo(p, judge, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                }
            }

            // 4. 荆棘 >= 10 施加止戈
            if (p.hasPower(ThornAuraPower.POWER_ID)) {
                int thorns = p.getPower(ThornAuraPower.POWER_ID).amount;
                if (thorns >= 10) {
                    addToBot(new ApplyPowerAction(m, p, new CeasefirePower(m, 1), 1));
                }
            }
        }
    }
}