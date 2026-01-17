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
    private static final int DAMAGE = 15;
    private static final int UPGRADE_PLUS_DMG = 10;
    private static final int STR_MULTIPLIER = 4;

    public TheTrial() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.RARE,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPGRADE_PLUS_DMG);
        setMagic(STR_MULTIPLIER); // 力量倍率通常不升级，如果需要升级可加参数
    }

    // --- 核心逻辑：4倍力量加成 (重刃逻辑) ---
    // 这种直接修改 baseDamage 的逻辑最好保持原样，而不是使用 BaseCard 的 VariableType
    @Override
    public void applyPowers() {
        AbstractPower strength = AbstractDungeon.player.getPower("Strength");
        int realBaseDamage = this.baseDamage;

        if (strength != null) {
            // 额外增加 (倍率-1) 倍的力量，因为游戏本体已经计算了1倍
            this.baseDamage += strength.amount * (this.magicNumber - 1);
        }

        super.applyPowers();

        this.baseDamage = realBaseDamage;
        if (this.baseDamage != this.damage) {
            this.isDamageModified = true;
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        AbstractPower strength = AbstractDungeon.player.getPower("Strength");
        int realBaseDamage = this.baseDamage;

        if (strength != null) {
            this.baseDamage += strength.amount * (this.magicNumber - 1);
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