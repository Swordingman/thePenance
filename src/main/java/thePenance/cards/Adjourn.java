package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.patches.PenanceHealPatches; // 引用 Patch
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class Adjourn extends BaseCard {

    public static final String ID = makeID("Adjourn");

    private static final int COST = 1;
    private static final int UPG_COST = 0;

    public Adjourn() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setCostUpgrade(UPG_COST);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int cardsPlayed = AbstractDungeon.actionManager.cardsPlayedThisTurn.size();

        if (p.currentHealth < (p.maxHealth / 2.0F)) {
            // 半血以下：【强制回血】
            // 使用自定义 Action 来绕过遗物拦截
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    PenanceHealPatches.forceRealHeal(p, cardsPlayed);
                    this.isDone = true;
                }
            });
        } else {
            // 半血以上：获得屏障
            addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, cardsPlayed), cardsPlayed));
        }

        addToBot(new PressEndTurnButtonAction());
    }
}