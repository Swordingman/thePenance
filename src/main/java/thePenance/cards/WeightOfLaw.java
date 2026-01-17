package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class WeightOfLaw extends BaseCard {

    public static final String ID = makeID("WeightOfLaw");

    private static final int COST = 1;
    private static final int HP_LOSS = 6;
    private static final int MAX_HP_GAIN = 2;
    private static final int UPG_MAX_HP_GAIN = 1; // 2->3

    public WeightOfLaw() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setMagic(MAX_HP_GAIN, UPG_MAX_HP_GAIN);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 失去生命
        addToBot(new LoseHPAction(p, p, HP_LOSS));

        // 增加最大生命 (使用 Action 队列以确保顺序，这里直接调用也可以，但放在 Action 更稳妥)
        addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.increaseMaxHp(magicNumber, true);
                this.isDone = true;
            }
        });
    }
}