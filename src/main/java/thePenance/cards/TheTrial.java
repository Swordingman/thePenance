package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class TheTrial extends BaseCard {

    public static final String ID = makeID("TheTrial");

    private static final int COST = 2;
    private static final int DAMAGE = 10;
    private static final int UPGRADE_PLUS_DMG = 5;

    public TheTrial() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.RARE,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPGRADE_PLUS_DMG);
    }

    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;

        AbstractPower strength = AbstractDungeon.player.getPower("Strength");
        int strAmt = (strength != null) ? strength.amount : 0;

        if (strAmt <= 0) {
            this.baseDamage = -strAmt;
        } else {
            this.baseDamage = (realBaseDamage * strAmt) - strAmt;
        }

        super.applyPowers();

        this.baseDamage = realBaseDamage;
        if (this.baseDamage != this.damage) {
            this.isDamageModified = true;
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;

        AbstractPower strength = AbstractDungeon.player.getPower("Strength");
        int strAmt = (strength != null) ? strength.amount : 0;

        if (strAmt <= 0) {
            this.baseDamage = -strAmt;
        } else {
            this.baseDamage = (realBaseDamage * strAmt) - strAmt;
        }

        super.calculateCardDamage(mo);

        this.baseDamage = realBaseDamage;
        if (this.baseDamage != this.damage) {
            this.isDamageModified = true;
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SMASH));

        // 2. 洗入抽牌堆逻辑
        this.purgeOnUse = true; // 本卡消耗（但不触发消耗相关的遗物，只是从战斗移除）

        // 创建副本洗入抽牌堆
        addToBot(new MakeTempCardInDrawPileAction(this.makeStatEquivalentCopy(), 1, true, true));
    }
}