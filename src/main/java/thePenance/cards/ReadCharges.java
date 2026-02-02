package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.util.CardStats;

public class ReadCharges extends BaseCard {

    public static final String ID = makeID("ReadCharges");

    private static final int COST = 1;
    private static final int JUDGE_AMT = 3; // 固定的裁决数
    private static final int DRAW = 1;      // 抽牌数 (Magic)
    private static final int UPGRADE_PLUS_DRAW = 1;

    public ReadCharges() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        // 设置魔法数字（抽牌数）
        setMagic(DRAW, UPGRADE_PLUS_DRAW);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得裁决
        addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, JUDGE_AMT), JUDGE_AMT));
        // 抽牌 (引用 magicNumber)
        addToBot(new DrawCardAction(p, magicNumber));
    }
}