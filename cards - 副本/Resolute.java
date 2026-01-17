package thePenance.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod;
import thePenance.actions.ResoluteAction;
import thePenance.character.Penance;

import static thePenance.PenanceMod.makeCardPath;
import static thePenance.PenanceMod.makeID;

public class Resolute extends CustomCard {

    public static final String ID = makeID("Resolute");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = makeCardPath("default.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION; // 别忘了这个

    private static final CardRarity RARITY = CardRarity.UNCOMMON; // X费卡通常是罕见起步，随你定
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Penance.Meta.CARD_COLOR;

    private static final int COST = -1; // -1 代表 X 费

    // 我们用 magicNumber 来存储“屏障倍率”
    private static final int BARRIER_MULTIPLIER = 4;
    private static final int UPGRADE_BARRIER_MULTIPLIER = 1; // 4 -> 5

    public Resolute() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = this.magicNumber = BARRIER_MULTIPLIER;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 调用我们自定义的 Action
        // 这里的 energyOnUse 是 X 费卡特有的变量，记录打出时剩余的能量
        addToBot(new ResoluteAction(p, this.magicNumber, this.upgraded, this.freeToPlayOnce, this.energyOnUse));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_BARRIER_MULTIPLIER); // 屏障倍率 4 -> 5
            this.rawDescription = UPGRADE_DESCRIPTION; // 切换到升级后的描述
            initializeDescription();
        }
    }
}