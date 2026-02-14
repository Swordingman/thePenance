package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.BarrierPower;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

public class Objection extends BaseCard {

    public static final String ID = makeID("Objection");

    private static final int COST = 1;
    private static final int DAMAGE = 9;

    // 效果数值
    private static final int EFFECT_SMALL = 4; // 裁决、荆棘
    private static final int UPG_EFFECT_SMALL = 1; // 2->3

    private static final int EFFECT_LARGE = 8; // 屏障
    private static final int UPG_EFFECT_LARGE = 2;

    public Objection() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE);

        int finalEffectS = EFFECT_SMALL;
        int finalEffectL = EFFECT_LARGE;

        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL){
            finalEffectS = 2;
            finalEffectL = 4;
        }

        // MagicNumber 用来存小数值 (裁决/荆棘)
        setMagic(finalEffectS, UPG_EFFECT_SMALL);
        // 自定义变量存大数值 (屏障)
        setCustomVar("LargeAmt", finalEffectL, UPG_EFFECT_LARGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

        // 判断意图
        if (m != null && m.getIntentBaseDmg() >= 0) {
            // 是攻击意图 -> 裁决
            addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, magicNumber), magicNumber));
        } else if (m != null && isDefendIntent(m)) {
            // 是格挡意图 -> 荆棘
            addToBot(new ApplyPowerAction(p, p, new ThornAuraPower(p, magicNumber), magicNumber));
        } else {
            // 其他 -> 屏障
            int barrier = customVar("LargeAmt");
            addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, barrier), barrier));
        }
    }

    // 辅助方法：判断是否是防御类意图
    private boolean isDefendIntent(AbstractMonster m) {
        AbstractMonster.Intent i = m.intent;
        return i == AbstractMonster.Intent.DEFEND
                || i == AbstractMonster.Intent.DEFEND_BUFF
                || i == AbstractMonster.Intent.DEFEND_DEBUFF;
    }
}