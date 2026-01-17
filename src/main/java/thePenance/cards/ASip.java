package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class ASip extends BaseCard {

    public static final String ID = makeID("ASip");

    private static final int COST = 1;
    private static final int TEMP_STR = 2;
    private static final int UPG_TEMP_STR = 1; // 2->3
    private static final int VULN = 2;

    public ASip() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        setMagic(TEMP_STR, UPG_TEMP_STR); // 临时力量
        setExhaust(true);
        setEthereal(true); // 虚无
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得临时力量
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber), magicNumber));
        addToBot(new ApplyPowerAction(p, p, new LoseStrengthPower(p, magicNumber), magicNumber));

        // 获得易伤
        addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, VULN, false), VULN));

        // 将一张复制品加入手牌 (makeStatEquivalentCopy 确保复制出的卡状态一致)
        addToBot(new MakeTempCardInHandAction(this.makeStatEquivalentCopy()));
    }
}