package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class WallBreakerSmash extends BaseCard {

    public static final String ID = makeID("WallBreakerSmash");

    private static final int COST = 1;
    private static final int DAMAGE = 18;
    private static final int UPG_DAMAGE = 6; // 18 -> 24
    private static final int BARRIER_COST = 10;

    public WallBreakerSmash() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(BARRIER_COST); // 用 Magic 显示消耗量
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 消耗屏障 (如果不够扣，就扣光，或者你可以加判定：必须够10才能打出)
        // 通常 "Consume 10" 意味着效果，而不是打出条件，这里直接扣除
        if (p.hasPower(BarrierPower.POWER_ID)) {
            addToBot(new ReducePowerAction(p, p, BarrierPower.POWER_ID, magicNumber));
        }

        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    // 可选：如果没屏障让它发光或者不能打出，这里暂定为直接打出并尝试扣除
}