package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class CaseReconstruction extends BaseCard {
    public static final String ID = makeID("CaseReconstruction");
    private static final int COST = 1;
    private static final int DRAW = 2;
    private static final int UPG_DRAW = 1; // 2->3
    private static final int SCRY_AMT = 5;

    public CaseReconstruction() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        setMagic(DRAW, UPG_DRAW);
        // 使用自定义变量显示预见数
        setCustomVar("Scry", SCRY_AMT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 预见 5 (ScryAction 自带“丢弃选中的牌”功能)
        addToBot(new ScryAction(customVar("Scry")));

        // 2. 抽牌
        addToBot(new DrawCardAction(p, magicNumber));
    }
}