package thePenance.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod;
import thePenance.actions.BlockToBarrierAction;
import thePenance.character.Penance;

import static thePenance.PenanceMod.makeCardPath;
import static thePenance.PenanceMod.makeID;

public class LawModification extends CustomCard {

    public static final String ID = makeID("LawModification");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    // 记得准备 LawModification.png，或者暂时用 Defend.png
    public static final String IMG = makeCardPath("default.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.UNCOMMON; // 罕见
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Penance.Meta.CARD_COLOR;

    private static final int COST = 2; // 费用 2
    private static final int UPGRADED_COST = 1; // 升级后 1

    public LawModification() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 调用转化动作
        addToBot(new BlockToBarrierAction());
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
            initializeDescription();
        }
    }
}