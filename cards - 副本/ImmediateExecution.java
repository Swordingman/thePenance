package thePenance.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod;
import thePenance.actions.TriggerJudgementAction;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;

import static thePenance.PenanceMod.makeCardPath;
import static thePenance.PenanceMod.makeID;

public class ImmediateExecution extends CustomCard {

    // ID: thePenance:ImmediateExecution
    public static final String ID = makeID("ImmediateExecution");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // 记得准备 ImmediateExecution.png
    public static final String IMG = makeCardPath("default.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.UNCOMMON; // 罕见
    private static final CardTarget TARGET = CardTarget.ENEMY;    // 目标：敌人
    private static final CardType TYPE = CardType.ATTACK;          // 类型：技能
    public static final CardColor COLOR = Penance.Meta.CARD_COLOR;

    private static final int COST = 1; // 1费

    private static final int MAGIC = 2; // 获得裁决数
    private static final int UPGRADE_PLUS_MAGIC = 1; // 升级 +1

    public ImmediateExecution() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得裁决 (先入队)
        addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, this.magicNumber), this.magicNumber));

        // 2. 触发裁决伤害 (后入队)
        // 这样执行的时候，玩家身上已经加上了刚才的 2 点裁决，伤害计算会包含这部分
        addToBot(new TriggerJudgementAction(m));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_PLUS_MAGIC); // 2 -> 3
            initializeDescription();
        }
    }
}