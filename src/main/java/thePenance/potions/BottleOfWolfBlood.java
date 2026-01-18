package thePenance.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import thePenance.PenanceMod;
import thePenance.actions.WolfCurseHelper;

public class BottleOfWolfBlood extends BasePotion {

    public static final String ID = PenanceMod.makeID("BottleOfWolfBlood");
    // 获取 UI 文本用于选卡界面的提示（见下文 JSON）
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PenanceMod.makeID("WolfCurseSelect"));

    public BottleOfWolfBlood() {
        super(
                ID,
                1, // 基础效果是选 1 张
                PotionRarity.RARE, // 稀有药水
                PotionSize.BOTTLE, // 普通瓶子形状，或者你可以选 H_SHAPED (心形/血瓶形)
                Color.FIREBRICK.cpy(), // 液体：深红/砖红
                Color.SCARLET.cpy(),   // 混合：猩红
                null                   // 无斑点
        );

        // 允许作为战斗外使用吗？通常“加入手牌”只能在战斗中
        this.isThrown = false; // 喝下去的，不是扔出去的
    }

    @Override
    public void use(AbstractCreature target) {
        // 只有战斗中能用
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            // 传入 potency (如果拥有神圣树皮，potency 会翻倍变为 2，玩家可以选择 2 张)
            String tip = uiStrings.TEXT[0];
            this.addToBot(new WolfCurseHelper.ChooseAction(this.potency, tip));
        }
    }

    @Override
    public String getDescription() {
        // 假设 JSON 是：选择 #b1 张 #y狼群诅咒 加入手牌。
        // 如果 potency > 1 (神圣树皮)，描述会自动变成：选择 #b2 张...
        return DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
    }
}