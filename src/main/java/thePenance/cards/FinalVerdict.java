package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.util.CardStats;

public class FinalVerdict extends BaseCard {

    public static final String ID = makeID("FinalVerdict");

    private static final int COST = 1;
    private static final int DAMAGE = 18;

    // 斩杀收益
    private static final int MAX_HP_GAIN = 3;
    private static final int UPG_MAX_HP_GAIN = 1; // 3->4

    public FinalVerdict() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.RARE,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE);
        setMagic(MAX_HP_GAIN, UPG_MAX_HP_GAIN);
        setExhaust(true);
    }

    @Override
    public void applyPowers() {
        int judge = 0;
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasPower(JudgementPower.POWER_ID)) {
            judge = p.getPower(JudgementPower.POWER_ID).amount;
        }
        int realBase = baseDamage;
        baseDamage += judge;
        super.applyPowers();
        baseDamage = realBase;
        isDamageModified = (damage != baseDamage);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int judge = 0;
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasPower(JudgementPower.POWER_ID)) {
            judge = p.getPower(JudgementPower.POWER_ID).amount;
        }
        int realBase = baseDamage;
        baseDamage += judge;
        super.calculateCardDamage(mo);
        baseDamage = realBase;
        isDamageModified = (damage != baseDamage);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateCardDamage(m);
        // 使用自定义 Action 处理斩杀逻辑
        addToBot(new FinalVerdictAction(m, new DamageInfo(p, damage, damageTypeForTurn), magicNumber));
    }

    // --- 内部 Action: 斩杀逻辑 ---
    public static class FinalVerdictAction extends AbstractGameAction {
        private final DamageInfo info;
        private final int maxHpAmount;

        public FinalVerdictAction(AbstractMonster target, DamageInfo info, int maxHpAmount) {
            this.target = target;
            this.info = info;
            this.maxHpAmount = maxHpAmount;
            this.actionType = ActionType.DAMAGE;
        }

        @Override
        public void update() {
            if (target != null && !target.isDying && target.currentHealth > 0) {
                // 显示特效
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, AttackEffect.SLASH_HEAVY));

                // 造成伤害
                target.damage(info);

                // 判定是否死亡 (注意：LastDamageTaken > 0 防止鞭尸)
                if ((target.isDying || target.currentHealth <= 0) && !target.halfDead && !target.hasPower("Minion")) {
                    AbstractDungeon.player.increaseMaxHp(maxHpAmount, true);
                }
            }
            this.isDone = true;
        }
    }
}