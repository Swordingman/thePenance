package thePenance.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod;
import thePenance.character.Penance;
import thePenance.powers.CodeOfRevengePower;

import static thePenance.PenanceMod.makeCardPath;
import static thePenance.PenanceMod.makeID;

public class CodeOfRevenge extends CustomCard {

    public static final String ID = makeID("CodeOfRevenge");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = makeCardPath("default.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.RARE; // 稀有
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;      // 能力
    public static final CardColor COLOR = Penance.Meta.CARD_COLOR;

    private static final int COST = 2; // 2费
    private static final int UPGRADED_COST = 1; // 升级后1费

    private static final int BARRIER_GAIN = 3; // 每次获得 3 点

    public CodeOfRevenge() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = this.magicNumber = BARRIER_GAIN;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new CodeOfRevengePower(p, this.magicNumber), this.magicNumber));
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