# BetterTips

> Great!
> ---- Klee

## 作用：

理论上来说，使用这个Mod在（服务器/客户端）可以实现非常不错的死亡消息和登陆消息以及聊天前缀。

这个Mod和其他Mod不同的一点在于他的配置文件非常自由，可以实现多种功能，比如对特定的玩家使用不同的颜色和不同的消息等等，甚至还支持随机获取消息等等奇奇怪怪的功能。

## 使用方法:

### 玩家使用

op玩家允许使用`/setDeathGlobal <message/color> [somemessage/somecolor]`以进行全局的提示更改

正常玩家玩家允许使用`/setDeath <message/color> [somemessage/somecolor]`以进行自己的提示更改

### 服务器使用

BetterTips为各位服主可以更好的管理服务器，支持使用配置文件修改。配置文件文件夹`bettertips`
应当存在于游戏运行目录（客户端.minecraft目录或服务端与server.jar同级）

打开此文件夹，理论上可以看到`death.config.yaml`，这个是死亡信息的配置文件

*注意：此文件需要运行游戏以进行生成*

文件内容大致像这样

```yaml
global:
  death.attack.mob:
    color: "very_dark_colorful"
    message: "有一个叫做${departed}的蠢材被${killer}暗杀"
  death.attack.lava:
    color: "colorful"
    message: "${departed}被岩浆侵入了"
  death.attack.player.item:
    color: "colorful"
    message: "有一个叫做${departed}的蠢材被${killer}用${weapon}暗杀"
  death.attack.player:
    color: "red"
    message: "有一个叫做${departed}的蠢材被${killer}暗杀"
player:
  liangmimi:
    death.attack.lava:
      color: "colorful"
      message: "有一个叫做${departed}的蠢材被岩浆烫死了"
    death.attack.lava.player:
      color: "blue"
      message: "有一个叫做${departed}的蠢材在被${killer}追赶的时候滑入岩浆而死"
```

- global 字段用于存储对未特殊注明玩家对默认死亡信息 (不用default是因为java关键字）

- player 字段指的是玩家配置部分

- 允许使用变量 杀手: `${killer}` 死者: `${departed}` 武器: `${weapon}`

- 死亡ID名请参考 [minecraft.wiki 死亡消息](https://zh.minecraft.wiki/w/%E6%AD%BB%E4%BA%A1%E6%B6%88%E6%81%AF)

- 颜色可参考 [minecraft.wiki 格式化代码](https://zh.minecraft.wiki/w/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81)

- (进行了一些测试，似乎minecoin_gold以后的颜色好像不行,原因未知)

- colorful 作为颜色指的是随机颜色

- 允许dark_colorful或者light_colorful，并且可以very_dark_colorful和very_light_colorful，可以制作更加炫酷的死亡消息

- 如果想要进行更好的自定义颜色可以复制转义字符,理论上§k这些转义字符也可以

可以进行修改文件以达到修改信息的效果
