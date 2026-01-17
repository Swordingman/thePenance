package thePenance.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.character.Penance;

import static thePenance.PenanceMod.makeCardPath;
import static thePenance.PenanceMod.makeID;

public class TheTrial extends CustomCard {

    public static final String ID = makeID("TheTrial");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = makeCardPath("default.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Penance.Meta.CARD_COLOR;

    private static final int COST = 2;
    private static final int DAMAGE = 15;
    private static final int UPGRADE_PLUS_DMG = 10; // 15 -> 25
    private static final int STR_MULTIPLIER = 4; // 力量倍率

    public TheTrial() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseDamage = DAMAGE;
        this.magicNumber = this.baseMagicNumber = STR_MULTIPLIER;
        // 标记：如果你想让这张卡在打出后不进入弃牌堆，而是彻底消失（为了配合洗入牌堆逻辑）
        // 可以在 use 中动态设置 purgeOnUse
    }

    // --- 核心逻辑：4倍力量加成 ---
    // 参考了原版战士卡牌【重刃】(Heavy Blade) 的写法
    @Override
    public void applyPowers() {
        // 1. 获取基础力量
        AbstractPower strength = AbstractDungeon.player.getPower("Strength");
        int realBaseDamage = this.baseDamage;

        if (strength != null) {
            // 游戏默认会加 1倍 力量。我们需要额外加 (倍率-1) 倍。
            // 比如 4倍，就是额外加 3倍。
            this.baseDamage += strength.amount * (this.magicNumber - 1);
        }

        // 2. 让系统计算其他的加成（比如虚弱、易伤、姿态等）
        super.applyPowers();

        // 3. 还原 baseDamage，防止显示错误
        this.baseDamage = realBaseDamage;

        // 4. 如果伤害被修改了，更新描述显示为高亮
        if (this.baseDamage != this.damage) {
            this.isDamageModified = true;
        }
    }

    // 这一步也是必须的，为了在选定怪物时准确显示伤害
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
        // 方法：设置本卡打出后直接消失(Purge)，然后生成一张一样的卡塞入抽牌堆并洗牌
        this.purgeOnUse = true;

        // 创建副本，true, true 代表 (洗牌, 自动位置动画)
        addToBot(new MakeTempCardInDrawPileAction(this.makeStatEquivalentCopy(), 1, true, true));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_PLUS_DMG);
            initializeDescription();
        }
    }
}