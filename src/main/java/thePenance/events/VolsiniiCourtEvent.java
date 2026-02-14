package thePenance.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import thePenance.PenanceMod;
import thePenance.monsters.VolsiniiCivilian;
import thePenance.monsters.VolsiniiMob;
import thePenance.relics.BloodstainedCloth;
import thePenance.relics.LetterOfGratitude;

public class VolsiniiCourtEvent extends AbstractImageEvent {
    public static final String ID = PenanceMod.makeID("VolsiniiCourtEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    // 静态变量务必在构造函数重置，防止跨局污染
    public static boolean civilianDied = false;

    // 0: 初始, 1: 战斗中, 2: 离开, 3: 战斗胜利结算(领奖)
    private int screenNum = 0;

    private float playerOriginalX;
    private float playerOriginalY;

    public VolsiniiCourtEvent() {
        super(eventStrings.NAME, eventStrings.DESCRIPTIONS[0], "thePenance/images/events/court.jpg");
        civilianDied = false; // 重置状态
        this.imageEventText.setDialogOption(eventStrings.OPTIONS[0]); // 介入战斗
        this.imageEventText.setDialogOption(eventStrings.OPTIONS[1], new com.megacrit.cardcrawl.cards.curses.Regret()); // 无视
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        System.out.println("VolsiniiCourtEvent: buttonPressed=" + buttonPressed + ", screenNum=" + screenNum);
        switch (screenNum) {
            case 0: // 初始界面
                if (buttonPressed == 0) {
                    // --- 玩家选择了战斗 ---
                    screenNum = 1;
                    civilianDied = false;

                    // 1. 保存位置
                    playerOriginalX = AbstractDungeon.player.drawX;
                    playerOriginalY = AbstractDungeon.player.drawY;

                    // 2. 移动玩家
                    float centerX = Settings.WIDTH / 2.0F;
                    AbstractDungeon.player.drawX = centerX;
                    AbstractDungeon.player.hb.move(centerX, AbstractDungeon.player.hb.cY);

                    // 3. 生成怪物 (使用之前修正过的逻辑)
                    float distance = 400.0F * Settings.scale;
                    float civilianOffset = 200.0F * Settings.scale;

                    VolsiniiMob leftMob = new VolsiniiMob(0.0F, 0.0F, VolsiniiMob.TYPE_AGILE);
                    VolsiniiMob rightMob = new VolsiniiMob(0.0F, 0.0F, VolsiniiMob.TYPE_HEAVY);
                    VolsiniiCivilian civilian = new VolsiniiCivilian(0.0F, 0.0F);

                    leftMob.drawX = centerX - distance;
                    leftMob.hb.move(leftMob.drawX, leftMob.hb.cY);
                    leftMob.flipHorizontal = false;

                    rightMob.drawX = centerX + distance;
                    rightMob.hb.move(rightMob.drawX, rightMob.hb.cY);
                    rightMob.flipHorizontal = true;

                    civilian.drawX = centerX - civilianOffset;
                    civilian.hb.move(civilian.drawX, civilian.hb.cY);
                    civilian.flipHorizontal = false;

                    MonsterGroup monsters = new MonsterGroup(new com.megacrit.cardcrawl.monsters.AbstractMonster[]{
                            leftMob, rightMob, civilian
                    });
                    AbstractDungeon.getCurrRoom().monsters = monsters;

                    // 4. 设置奖励：关键点！
                    AbstractDungeon.getCurrRoom().rewards.clear();
                    AbstractDungeon.getCurrRoom().addGoldToRewards(30);
                    // 【重要】不要在这里 addRelicToRewards，否则会在战斗结算界面直接给玩家，而不是回到事件给

                    // 5. 进入战斗
                    enterCombatFromImage();

                } else {
                    // --- 玩家选择了离开 (获得悔恨) ---
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                            new com.megacrit.cardcrawl.cards.curses.Regret(),
                            Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    this.imageEventText.updateBodyText(eventStrings.DESCRIPTIONS[1]);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(eventStrings.OPTIONS[2]); // [离开]
                    screenNum = 2;
                }
                break;

            case 3: // 领奖阶段
                // 1. 发放遗物 (保持不变)
                if (!civilianDied) {
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                            Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new LetterOfGratitude());
                    this.imageEventText.updateBodyText(eventStrings.DESCRIPTIONS[4]);
                } else {
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                            Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new BloodstainedCloth());
                    this.imageEventText.updateBodyText(eventStrings.DESCRIPTIONS[5]);
                }

                // 2. 变成离开按钮
                // 依然使用 update，不要 clear + set
                this.imageEventText.updateDialogOption(0, eventStrings.OPTIONS[2]);
                this.imageEventText.clearRemainingOptions();

                // 3. 状态流转
                screenNum = 2;
                break;

            case 2: // 离开阶段 (玩家点击了 OPT[2])
                openMap();
                break;
        }
    }

    /**
     * 战斗结束（玩家点击"前进"）后，游戏会自动调用此方法
     */
    @Override
    public void reopen() {
        if (screenNum != 1) return; // 只有从战斗(状态1)回来才执行

        // 1. 还原玩家 (参考 Colosseum)
        AbstractDungeon.resetPlayer();
        AbstractDungeon.player.drawX = playerOriginalX;
        AbstractDungeon.player.hb.move(playerOriginalX, playerOriginalY);
        AbstractDungeon.player.flipHorizontal = false;

        // 2. 【核心】调用父类方法恢复界面
        // 这行代码会自动处理 RoomPhase 和 UI 的显示，不要自己去 set phase 或者调 show()
        this.enterImageFromCombat();

        // 3. 设置状态以便 buttonEffect 响应
        screenNum = 3;

        // 4. 准备文本
        String bodyText;
        String optionText;

        if (!civilianDied) {
            bodyText = eventStrings.DESCRIPTIONS[2];
            optionText =  eventStrings.OPTIONS[3];
        } else {
            bodyText = eventStrings.DESCRIPTIONS[3];
            optionText = eventStrings.OPTIONS[4];
        }

        // 5. 更新 UI (参考 Colosseum)
        // 不要用 setDialogOption，而是更新现有的第0个按钮
        this.imageEventText.updateBodyText(bodyText);
        this.imageEventText.updateDialogOption(0, optionText);
        this.imageEventText.clearRemainingOptions(); // 清除掉可能多余的按钮
    }
}