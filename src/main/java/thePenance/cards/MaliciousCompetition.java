package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class MaliciousCompetition extends BaseCard {

    public static final String ID = makeID("MaliciousCompetition");

    private static final int COST = 1;
    private static final int DAMAGE = 12;
    private static final int BARRIER = 8;

    public MaliciousCompetition() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE);
        // 屏障数额固定为8，若需升级可自行添加 setMagic
        setCustomVar("Barrier", BARRIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 造成伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

        // 判定条件
        float threshold = p.currentHealth;
        // 升级后，阈值变为玩家血量的一半
        if (upgraded) {
            threshold = threshold / 2.0F;
        }

        // 如果目标剩余血量 > 阈值
        if (m.currentHealth > threshold) {
            int barrier = customVar("Barrier");
            addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, barrier), barrier));
        }
    }
}