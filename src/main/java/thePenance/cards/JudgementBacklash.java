package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.util.CardStats;

public class JudgementBacklash extends BaseCard {
    public static final String ID = makeID("JudgementBacklash");
    private static final int COST = 1;
    private static final int DAMAGE = 3;
    private static final int UPG_DAMAGE = 2; // 3->5
    private static final int THRESHOLD = 3;  // 每3层

    public JudgementBacklash() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(THRESHOLD);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 基础伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

        // 2. 额外触发逻辑
        if (p.hasPower(JudgementPower.POWER_ID)) {
            int judgeAmt = p.getPower(JudgementPower.POWER_ID).amount;
            int times = judgeAmt / magicNumber;

            if (times > 0) {
                for (int i = 0; i < times; i++) {
                    // 稍微加点间隔，让多段攻击有节奏感
                    addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
            }
        }
    }
}