package thePenance.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import thePenance.PenanceMod;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;

import static thePenance.PenanceMod.makeCardPath;
import static thePenance.PenanceMod.makeID;

public class Sting extends CustomCard {

    public static final String ID = makeID("Sting");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = makeCardPath("default.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Penance.Meta.CARD_COLOR;

    private static final int COST = 1;

    private static final int HP_COST = 3;
    private static final int THORNS_AMT = 3;
    private static final int UPGRADE_THORNS_AMT = 3;
    private static final int JUDGE_AMT = 2;

    public Sting() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = this.magicNumber = THORNS_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new LoseHPAction(p, p, HP_COST));

        addToBot(new ApplyPowerAction(p, p, new ThornAuraPower(p, this.magicNumber), this.magicNumber));

        int judgeGain = this.upgraded ? 4 : 2;
        addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, judgeGain), judgeGain));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_THORNS_AMT);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}