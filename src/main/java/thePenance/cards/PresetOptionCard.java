package thePenance.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod;
import thePenance.character.Penance;
import thePenance.character.PenancePresetHelper;
import thePenance.util.CardStats;

@AutoAdd.Ignore
public class PresetOptionCard extends BaseCard {

    public static final String ID = PenanceMod.makeID("PresetOptionCard");

    private static String getImagePath(PenancePresetHelper.PresetLevel level) {
        switch (level) {
            case REHEARSAL:
                return "thePenance/images/cards/skill/CourtRehearsal.png";
            case WOLVES:
                return "thePenance/images/cards/skill/SyracusanWolves.png";
            case CURSES:
                return "thePenance/images/cards/skill/Upright.png";
            case DRINK:
                return "thePenance/images/cards/skill/ASip.png";
            case HEALTH:
                return "thePenance/images/cards/skill/WeightOfLaw.png";
            case DEBATE:
                return "thePenance/images/cards/skill/Debate.png";
            case DEFAULT:
            default:
                return "thePenance/images/cards/skill/Resolute.png";
        }
    }

    public PresetOptionCard(PenancePresetHelper.PresetLevel level) {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.SPECIAL,
                CardTarget.NONE,
                -2
        ), getImagePath(level));

        String targetID;
        switch (level) {
            case REHEARSAL:
                targetID = PenanceMod.makeID("PreRehearsal");
                break;
            case WOLVES:
                targetID = PenanceMod.makeID("PreWolves");
                break;
            case CURSES:
                targetID = PenanceMod.makeID("PreCurses");
                break;
            case DRINK:
                targetID = PenanceMod.makeID("PreDrink");
                break;
            case HEALTH:
                targetID = PenanceMod.makeID("PreHealth");
                break;
            case DEBATE:
                targetID = PenanceMod.makeID("PreDebate");
                break;
            default:
                targetID = PenanceMod.makeID("PreDefault");
                break;
        }

        CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(targetID);
        this.name = cardStrings.NAME;
        this.rawDescription = cardStrings.DESCRIPTION;

        this.initializeTitle();
        this.initializeDescription();
    }

    @Override
    public void upgrade() {}

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}
}