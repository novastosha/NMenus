# NMenus
## About the project

This project was made to help people who want to start making their servers, just by configuring some human-readable files, which will result in a baiutiful menu.

### How to create a simple menu

First off start by making **A '.yml' file** named whatever you want, located in: *"./plugins/NMenus/menus/"*

Now, we need to start configuring the file, so the plugin loads it properly.

First off, we started by adding a code-name to our menu configuration file... __*(Codename works as a special identifier for a menu)*__

```yml
code-name: test
```
Next, we give our menu a name; this value is the "inventory" title.

__NOTE:__ For color codes use the special symbol "§" and quote the value with '"'

```yml
display-name: "§aHello!"
```

It is necessary to give our menu an inventory type.

__List of inventory types:__

```
- CHEST
- CRAFTING
```

__NOTE:__ When using *"CHEST"* inventory type, make sure to add the __*integer*__ value "rows" to tell the plugin how big your inventory is.

So we do:

```yml
inventoryType: CHEST
```

*And because the inventory type is 'CHEST' we add:*

```yml
rows: 3
```
