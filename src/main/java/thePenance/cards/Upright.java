package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class Upright extends BaseCard {

    public static final String ID = makeID("Upright");

    private static final int COST = 2;
    private static final int UPGRADED_COST = 1;
    private static final int BASE_STR = 1;

    public Upright() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setCostUpgrade(UPGRADED_COST);

        // 设置一个自定义变量来显示总力量获取数，基础是1
        setCustomVar("StrGain", VariableType.MAGIC, BASE_STR, (card, m, base) -> {
            AbstractPlayer p = AbstractDungeon.player;
            int curseCount = 0;

            // 统计诅咒数量
            curseCount += countCurses(p.hand.group);
            curseCount += countCurses(p.drawPile.group);
            curseCount += countCurses(p.discardPile.group);

            // 最终值 = 基础1 + 诅咒数
            return base + curseCount;
        });
    }

    private static int countCurses(java.util.ArrayList<AbstractCard> cards) {
        int count = 0;
        for (AbstractCard c : cards) {
            if (c.type == CardType.CURSE || c.color == CardColor.CURSE) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获取计算后的总力量值
        int totalStr = customVar("StrGain");
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, totalStr), totalStr));
    }
}