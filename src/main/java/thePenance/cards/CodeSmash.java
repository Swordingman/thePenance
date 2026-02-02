package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class CodeSmash extends BaseCard {

    public static final String ID = makeID("CodeSmash");

    private static final int COST = 0;
    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3;
    private static final int THRESHOLD = 7; // 每7点

    public CodeSmash() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPG_DAMAGE);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        // 我们需要把 DamageInfo 存下来，以便稍后引用
        DamageInfo info = new DamageInfo(p, damage, damageTypeForTurn);
        addToBot(new DamageAction(m, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        // 2. 结算回能
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 检查刚才那次攻击实际造成的伤害 (m.lastDamageTaken)
                // 注意：这必须紧跟在 DamageAction 之后
                if (m.lastDamageTaken > 0) {
                    int energyGain = m.lastDamageTaken / THRESHOLD;
                    if (energyGain > 0) {
                        addToTop(new GainEnergyAction(energyGain));
                    }
                }
                this.isDone = true;
            }
        });
    }
}