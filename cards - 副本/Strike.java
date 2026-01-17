package thePenance.cards; // 1. 这里的包名要改成你实际的包名

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod; // 引用主类
import thePenance.character.Penance; // 引用角色类

public class Strike extends CustomCard {

    // 2. 卡牌ID：必须要和 Character 类里 getStartingDeck 写的一模一样
    // 建议格式：ModID:CardName
    public static final String ID = PenanceMod.makeID("Strike");

    // 读取本地化文件里的文字
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = PenanceMod.makeCardPath("default.png"); // 图片路径，暂时没图可以用原来的

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    // 基础属性
    private static final CardRarity RARITY = CardRarity.BASIC; // 稀有度：基础
    private static final CardTarget TARGET = CardTarget.ENEMY; // 目标：敌人
    private static final CardType TYPE = CardType.ATTACK;      // 类型：攻击
    public static final CardColor COLOR = Penance.Meta.CARD_COLOR; // 颜色：斥罪专属色

    private static final int COST = 1;      // 费用
    private static final int DAMAGE = 6;    // 伤害
    private static final int UPGRADE_PLUS_DMG = 3; // 升级增加的伤害

    public Strike() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        // 设置基础伤害
        baseDamage = DAMAGE;

        // 这是一个Tag，标记它是“打击”牌（有些遗物会加成打击牌）
        this.tags.add(CardTags.STARTER_STRIKE);
        this.tags.add(CardTags.STRIKE);
    }

    // 当卡牌被使用时
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 造成伤害的动作
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    // 卡牌升级逻辑
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_PLUS_DMG);
            initializeDescription();
        }
    }
}