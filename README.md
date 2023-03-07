### Auctions watcher

It's a Telegram bot notifier for new items in selected auction marketplaces.

Supports pages from:
- [meshok.net](https://meshok.net/)
- [<s>avito.ru</s>](https://avito.ru/)
- [<s>kufar.by</s>](https://kufar.by/)
- [antiques.ay.by](http://antiques.ay.by/) 
- [etsy.com](https://www.etsy.com/)
- [ebay.com](https://www.ebay.com/) and [ebay.de](https://www.ebay.de/)
- [olx.pl](https://www.olx.pl/)



#### How to use

The program needs a configuration file in a json format. The file should be set in an environment variable as a string with a CONFIG_JSON name. 

Example:

``` json
{
  "userId" : "telegram-user-id", // as for now only one user notification is supported, user id has to be explicitly specified
  "watchers" : [
    {
      "token" : "telegram-bot-token", //telegram bot token in which the notifications are to be sent
      "name" : "ebay searches", 
      "pages" : [
        {
          "url": "https://www.ebay.de/sch/i.html?_from=R40&_nkw=alter+christbaum&_sacat=0&_sop=10",
          "description": "Ebay.de - запрос alter christbaum",
          "period": 10 //time interval for checking for the updates
        },
        {
          "url": "https://www.ebay.com/sch/i.html?_from=R40&_nkw=glass+garland&_sacat=907&_sop=10",
          "description": "Ebay.com - запрос Glass garland",
          "period": 30
        }
      ]
    },
    {
      "token" : "telegram-bot-token-2",
      "name" : "ay.by",
      "pages" : [
        {
          "url": "http://antiques.ay.by/retro-veschi/igrushki/yolochnye/?f\\=1&order\\=create&createdlots\\=1",
          "description": "Ay.by",
          "period": 1
        }
      ]
    }
  ]
}
```

