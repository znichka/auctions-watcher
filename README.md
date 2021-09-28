### Auctions watcher

It's a Telegram bot notifier for new items in selected auction marketplaces.

Supports pages from:
- [meshok.net](https://meshok.net/)
- [avito.ru](https://avito.ru/)
- [kufar.by](https://kufar.by/)
- [antiques.ay.by](http://antiques.ay.by/) 
- [etsy.com](https://www.etsy.com/)
- [ebay.com](https://www.ebay.com/) and [ebay.de](https://www.ebay.de/)



####How to use

The program needs a configuration file in json format. Example:

``` json
{ 
    "token":"token-of-the-telegram-bot",
    "userId":"telegram-user-id",
    "pages":[
        {"url":"url to the page",
        "description":"page description",
        "period":5}, // time interval for checking for the updates
        {"url":"url to the page 2",
        "description":"page description 2",
        "period":5}
    ]
}
```
The file should be set in an environment variable as a string and with CONFIG_JSON name 
