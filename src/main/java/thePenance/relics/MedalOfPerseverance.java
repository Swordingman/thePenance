package thePenance.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import thePenance.character.Penance;
import static thePenance.PenanceMod.makeID;

public class MedalOfPerseverance extends BaseRelic {
    private static final String NAME = "MedalOfPerseverance";
    public static final String ID = makeID(NAME);
    // 使用 SPECIAL 稀有度，这样它不会在商店或精英怪掉落池里出现
    private static final RelicTier TIER = RelicTier.SPECIAL;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public MedalOfPerseverance() {
        super(ID, NAME, Penance.Meta.CARD_COLOR, TIER, SOUND);
    }

    // 只需要写这一句，确保它的描述能正确显示
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}