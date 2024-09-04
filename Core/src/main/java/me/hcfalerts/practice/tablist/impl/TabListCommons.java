package me.hcfalerts.practice.tablist.impl;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.hcfalerts.practice.tablist.impl.utils.SkinTexture;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class TabListCommons {

    private final static Map<UUID, SkinTexture> cache = Maps.newHashMap();

    public static SkinTexture defaultTexture = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=",
        "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="
    );

    /*public static SkinTexture defaultTexture = new SkinTexture(
            "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=",
            "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="
    );*/

    public static SkinTexture TWITTER_TEXTURE = new SkinTexture(
        "ewogICJ0aW1lc3RhbXAiIDogMTYxMDE2ODEyOTE4MCwKICAicHJvZmlsZUlkIiA6ICJiOTY5NzViNTBiM2Y0N2RhOTUwYTM5MTgxNjU3MDZjZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJHZXBoIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E2MWJkNjI2MTIzNjQwMWVhZDJjZjNlMjg2MGU5ODk0NzA0MzcxNWMxMGM2ZmJlZWUwMzNlN2U3MTY3OGYzMDAiCiAgICB9CiAgfQp9",
        "evfnEVcNAkfOXORZquHSInNm1jWd3ANXUcIrY72GkBWqewbrfypYRzMqr1OE2e8XQ1XRrjy6/WmbgOXROC51KTA7Tu8A0X0mfaUfMd6seDRcjInMO4BCKdHBEcj2rj6Dr3kVzuFVRamuEgUNl8TnpXMXIk4Phg6Va78LRtp7o98an9b1K+PahDXGF//aAlGGtNXqAIa3um3FP87KPBn8Trakqs5QZ6zQNsUMJkCoeRWstdl1wul/vttugdbjvVM+EVgAtUhW3nQ+mQK9Lx2qe3t5nFWO+bUkuCL3IZS+nruLcsVK72rBmhA74BeT48AuI2w87TicoGc+4tNGjb8qDxCVt/Ycw1UKcVR+Ri5BJGvfQf0jphwrxiPegJi+JbzdTLHT1AfQN6lh1nNrB/0MsRb0RapphmLrM2fYY7oBoQQyaClwFu22YaWGmtqWRhwt9qu+GoZxgcJmxnm1/fMNhBq1RDS6RvzFe4Sc+GFawZEwlnRbMTETZfHZWzqakDs3YnmMCLMTuc18l3gO0zvqKj/k8Q4hV5CG9XvKttYNWel22YoD9EiW/FM33ICLxOvSjYrsEdEPt2vRfkfVLpl8jkItvyLQlJAQ6fP7bpOXU1ADpew/ug6kND3tsXX+eWNlxLGpLNm5GlQY66/uGhtHHOPb3etLie82CAkeX022Chw="
    );

    public static SkinTexture DISCORD_TEXTURE = new SkinTexture(
        "ewogICJ0aW1lc3RhbXAiIDogMTYxMDE2ODMyNjA3NSwKICAicHJvZmlsZUlkIiA6ICI2MWVhMDkyM2FhNDQ0OTEwYmNlZjViZmQ2ZDNjMGQ1NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVEYXJ0aEZhdGhlciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84YzUzN2I0MjMwYjFmMjE2MDU0NTUyODgzYjU1MDQwODg3MDAwNDNmZTgzYmM5ZDY0MjlhMTIyMjUzZjQ3ZjJiIgogICAgfQogIH0KfQ==",
        "pf/G7DG9uWfLp2msnqOH4R5YX8k/oHqGGKS0PtspdQRkfsMPkp5XiZW+HJNF3P0go0Cmoq33Q2ogVxfXH/8W2gXP+5qcNDd4zlNUsKMLTzoHXEB/ZwX7ESyz4qEzq+CRCdjoavRfyvZL+g94SqU8TmYuHSUBzOEJeaIwFiVZAaw7fTVj5dprknpvMHri9CaawgxszAOz0LsWPxgbZVLiprBPyjbDiIXOR+yFB8pdnLYX3p2WsQ4vRAbNCNrOutS3pOyqMnIbfaLjt+hwHK79cALh/TdPi9nPujLj/GIW7ZW3kqeSBd11j8hfZ2W3Vfmr8h4yEbJed1tKFkiCJ/4eT6C/MKGEnQRQ3mDjRuDzuY+LjroEa32jS8YEbguUoQxrphP4LDOB4x4yMaL4HWVpCT5yLlV2ORfgCk+UKhmsciZCr5Dx7gpjdCsVUErMxGgnxJuGeZCKt46SeD8aUcB+NooueRGHUCjPEVT+gX1bw2Ndw52Hjbxm6PgOabc9dYPGSUejIyDaZx4MgVjOqvJOB++nhDTT8zbpkyNpTY6no24TXcOkEpL9PMpxjQcg+WawdegrT+IJvpaB16SaBkltgnLhL+FyO6+/sKLgvC8Y+oI7lgTHhlrt0eUfHMoIKP4cv3ELVsiCOVTdl9LNw6IWPlxzlb7ZFxczuwMwuIXqlpo="
    );
    public static SkinTexture CHEST_TEXTURE = new SkinTexture(
        "ewogICJ0aW1lc3RhbXAiIDogMTYyMTM4NjY3MDA5NCwKICAicHJvZmlsZUlkIiA6ICIzMDNkNTYyNDc5NmY0MGVkOGQ5NDA3MzE1YTQ2ZTNiYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUb20yNVciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk4ZDU1ZjFjOWNiMmMzYmMzMjhmMGVhZTU3ZjZjOTI3ZmNmYzRhZmUwYThhNTA5NTM5ZGU4ZjJlZmExMTI1NyIKICAgIH0KICB9Cn0=",
        "H8IZCFVjU3hh8KcHdg98FmzFI/q/fbszYK2rn+jO7E3EPSYl/ifp1MVw47HaTBxuKrTvXJo0heq+AGtkAePTIzC1MtSfSAcAtS7Mv9ynz2VPOgsPNJZO1U41A2dbC7q/Uy+9dh6XCVVtm3lzOqQLHMbjI0Lh9TSJjp1XFnHHPsOfnR/Pa/73rW9jMd6nJHaeYOrFcBd1jIFQW+jHZG15zfYDNYj5ag9Y/qUWbzQuKJ2BRCHVeqXzwSEqOKUgUQguQNpxTjtVnfv+RxDsMg2EEpscmDCPiPlLxTlCKqviH8AZDF4ipC+tUg/Z90eL/wVhArl2sqKwBKwpuI73v2OtJNHr94mgHCzpa0z60uM6M2maWGoTB5MgNFNfpGpAY3H71wwCYSbov4WrbPriebtU/Hxv3jAtHULXyfumK8d5MT324pp72MxwyPiFOplBOX8N4bI8BhF4tMrwa58ZcJ1TesHxoZvG5oBBnikULsh/3N9IPRuGNgfJAR/9ogFSsDKLnDuxo6376LxSqT9fiMk5e/tZ757eHnFMMAYEezFb37stcYkenHSrBLyHn208XDcM4gUVkVNnYPlLiKx2wy+JZjp9aqgslpaa1sJh3p7i9M9XFHaUvOMN8gkbm6T4WjbD939XvBnZBHfMC887XDTRyFUbyNxXcfkwA5f5rtOcqNQ="
    );

    public static SkinTexture STEVE_TEXTURE = new SkinTexture(
        "ewogICJ0aW1lc3RhbXAiIDogMTYyMTM5MzkxNjk3MywKICAicHJvZmlsZUlkIiA6ICIzMDJjZmNjOWRmZTE0NDlmOTc1NThiM2U2MGEyNzU2YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdXBlclN3ZWF0eSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85ZmI3ZWQ5ZmFmZTM4YTFkOTMzMDFmMTEwZWFmMTM3ZjYxODgzMTY0MzhiMDVkMjEzYzg2MjA3NmFmNzk2ZjBmIgogICAgfQogIH0KfQ==",
        "KApoGcycMAMzgyZGCqf59Cu60eleW+NL6jcXI3NgAc96t/ApG/BvCgVuTKDpOsfQuRuo5MvJ69jWK2opmieeW1sY6fzKhia5ae3C3Uhel+69N9+PdjQ/uvIB6oQ3tcPdL7E0G+Q0Lc7Cx89hjbF8RAcTrmw8zcyNWTd1kxVHf6bus08qw4eF0oTUQaE8iFtViFWC1qPzaSGe94p4gi/mB8+7itmroo4T1NQSm0yOzk+5e4Mo+C3Ev3YRu2x7D1h9fgoxwi+vAbisKK1Taa+xPJDL63Hl6Z44fiVqFtIIWsl6bIVw2q16BGgPCUPX+PLitOqU5Q+MO/D2xqeeDmpvHWGfnaC/Yku9IdyqAylDWyuwiwX8qyYBV0G6pi1L58i63xNiE1aJjg7/yctW8eYtXj+z63hhUvc5y5qrtMyNEsOJfPljEWMNOjwsGNfg8qQfMXR1ufxROEPNoSlMaiFa1fSYmIENXktEHfeZ1nb3034cv769wGrW2de7L59dhGkQwqTglKFPYPpDKmXvl0KICLEdS6EUylRs7jyzfBxvQx1diC8+SW0D8pILsQJzpUgxntY/toPjILUex+HYMsOMGcNZo0m3rKSGuKWUlEG4CDR3cc0Zvj3OPOsl7kdPp69UZS76ZRydAraiJxc1PD5P7kbrHus3CXuHMbX9D8+Z3FM="
    );

    public static SkinTexture CLOCK_TEXTURE = new SkinTexture(
        "ewogICJ0aW1lc3RhbXAiIDogMTYyMTM5NTM2MjI2MiwKICAicHJvZmlsZUlkIiA6ICJhMzA1ZGZkNDQ4ZWM0NDVjYjNmNDQyZGIyZTBiZTVmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJEYXZpZEdyaWZmaXRocyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84MDM4MzlmNTQzNDE0YTBmYTcwYmNlYzEwZWVkZGM4ZWJhNTkzNjRiOGI1OTJiZDA3YzUwNzc3MjhmNzUzNjM3IgogICAgfQogIH0KfQ==",
        "hxEifv402Nywid3ahfOs5Pnv08AgyZxlALOtR7gH/xxFR5zSU3/JdYZp6oM9nCzHGfKsvwb8qEvpsXpHbF63x/bzAmb8S/5M4GnciaPx+gdfrNWq00L8nyGLyNc62hNWhyj1dsb9mCJ27evrXeVlVUzXwEg/rYkPJuwEGkzTEOjUaFIerQbILXtfvs6vJV+KjBT4hDxd2s1JH4T5zqRRiiBx5HqYCzevj2aRdYuxbODjfIguZo2dYR7fTiqCfB3wDe7BkpX4MxZtpczg/5q36qcGMbMD0KIQxWuy6FhVThv+2H+FD7Ugx3bgaG1gmwiZy2yF9yd9UVV/uDn9f17OWJ5zNX0QspQRQvKP5+jY/kb4si4PNZVXC/bcqo7fFMKsymcMGmY5hiM3SSYR3+poXFJ6WDPKoEPpdeLTvczfrKDolWcQmrVdkE/Rt85mNT/WEQnrEZqCWLdWIj+Kz0QaYLNjUCuvVR+23E9KEhumU9KP4gXv5da/h1OVHpX1P4vs85/VZQMqk6cZDPr2XyllvO9VkTFNeCiQBe2MT5GumWD9swq+NFtyhG8+hKRi0ujpdX8qn5nSN0LWkAuV02JRkrRHlPZlo23QmiHeNljPy8xq+cWSZB4JueE3VY/l8inYa/Ukt9CH242qDdHm73qI1fJ8/IgNILYxLSGuoN4jpnE="
    );

    public static SkinTexture DIRT_TEXTURE = new SkinTexture(
        "ewogICJ0aW1lc3RhbXAiIDogMTYyMTM5NTA4MzI2NSwKICAicHJvZmlsZUlkIiA6ICIyZjhmZjkwOWRhNjY0ODMzODgzZjExODcxNDQxYjkwYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb29iaWUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY5NDkwYzQ3YjY3MzBhYjFkZGE5NTNhOGQ5ZTYzMzZkNWE4MTIxYmQyMGIzYzFhMDRmOWJkMDc5MTFmMmEwYiIKICAgIH0KICB9Cn0=",
        "aJCnpFZj8JfRvIQerm6KjesWcIY19FEGnGbi0vKzIafBqT6gg88maSO3bCwmsj0w9bvJ1a0yUyG+aEZTbEr17c/t9YUUi3DJz6Nmdo4xZqo7OeTGDauRTDpq03EOFlp7LsRWTlGnaklmsHKg7Xx+PsMKigk6zKxaxcdpDI7mGuokDKtd4P9U9NzNsukN5kSM3Rwn+bnROY7cT0cONs1nR4VClF1uHFOnFnGPqzqr7DA9D1WxupbndnndbWJ6IIfB0ljOPSA6pevfg03igQOmvSxbsWEJJQ517V0iCbktzFf7M2JKhdMsYIU49tfzv3eu+Y5RigzCt2pi+G+4CjImTQO+4Gvf36DbWfjCHQqp4x9ytyBSZANj4ANJuqiNGmAne/a8/b2ntlh/L56tPnKCXxUrCWG+JTzbweSQdGqtVexTxHr2AmAdnei4fDi8xK3Lfg+Iug3opeE/i4sajIGpwwkgHz+wsoDI2XeKkwqv56rNg3CbHdIHQsluVESWrdG7FE7n1cEOqRHeJZQyyKwcwrQmpQyDIYRFCrYTuqKMrNyVkjBgEeJeojJWGJozv4OF7LXlithOufGtI0MMJWD0imDTgGjRvUesQKciwSqD4A3l1gK1J19sLYOnnAOcCvvzFa37JLXbQ3uHqsVwjHYLB2dXRb6C/aApdrav8sHOcXY="
    );

    public static SkinTexture PING_TEXTURE = new SkinTexture(
        "ewogICJ0aW1lc3RhbXAiIDogMTYyMTM5NzAzNzEwMiwKICAicHJvZmlsZUlkIiA6ICI1NDgwOTFkODY1NmY0ZTlmYTg1OTBkNGRjYTJjNGMzZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBZGRlbGJ1cmdoIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzcxZWFkMTZkOTQwNDU4NzNkYTg2MWFiZjk0NzAzZTQ4OWY5Y2Y0OThkYjVhMmEzM2E0MWNhYmRhOTNhZTE4MjUiCiAgICB9CiAgfQp9",
        "Txbv3bamQ6XxN4RV7Inbz8VvuSARa+M17y/uzx8rZiYA+C+SS3s25AxD1eqGJ3BC02l9JXSqqHvCEyPgw6VCDRbrg6SCH0LL+5gWL3PW47F6ZjDT3NflK1WupHFw/WxjMuM2IQOslva8Nycz5AJ6WyqEdPu3f0cXx0HdX6qOBqdL56+BRkWR1bxEaF1hAXDJfCQuQswDFKVMR6MgqhKpdJB1H8557GSBoIwLcSYpwRVxilV6y5jb1FEpksz8M4f+YliYsFvJkfGBlkZQYurrhrsQK7wWdzzIzab9gRCtZ3pOkFJ8XPkLieI/Yu5gkKWlvwgSqiUWvqLFLYJczOpIcT+UECZA8d9g5BWteRAfvJ3c+m1EOkjkjfnZT+DsL1dDb+Ww1vuS62uOcyYBiYwbqNNwJ7drCVofyDxvV/m7IkZNmeDV5CgRSxpCf7THvJcZeQIQlqZ0bDKV712L6aPppS3DkGnCmjYzUh+RQKriYTf6EJvGVGR91EwrO9/EDCLGWJoncxrEv7ZsbHgRRVofm6wwAPFwk4724jPUO6srAQLDA/7z8yGHJbQ80zwCmen7h9DZqpxZ8UKM/uRlYEMUtJON5Q+rlqFivhBI8BsAkXxJoIKBoQ9ddS/O7lFhhKT1IPKETcRZ4nu7kz3m8INTgZOck1n/u2rttnka42+LB+g="
    );

    public static SkinTexture TV_TEXTURE = new SkinTexture(
        "ewogICJ0aW1lc3RhbXAiIDogMTYyMTM5NTczNDk5NCwKICAicHJvZmlsZUlkIiA6ICIyNjlhOTA0ZTJkOGQ0YjEzYjlhNTZiNzVmMWNkYmQ2NCIsCiAgInByb2ZpbGVOYW1lIiA6ICIwbHZpMTExIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RjMjUyZjUyYTVmYjg0NmM0NWZkMTA2ZGRlZWI1MDQyMjdhMmYyYzk3ZTgxNGZlYTUwMWUwY2Y4ODhmZjM2MzgiCiAgICB9CiAgfQp9",
        "frn53kOj51bJH6E5aVR+fkPT1nuMKd0UMh2CBeAulSJFoLknmnUae78JwKsBOatA+fp+Ye+cgW4SZEw3F/qG9Eyc/SxmU0M2c7S38rdq2C566YBQbE9cYwG7sPbG8FJfpubTTcsMw/OuM+MJR2Aro+ra9G6dOJIs90gDWeG+gEa1VEZ26TtTR+IjTzza6w3KqmxC/25f9+YWaDpvCpluAfc3UGyRrjoBWjUpju/7qLYdvJmQcP+LZtZqQRcWEMxHZ9UWpiV/EnIirXlIOetRRZAhTR7j3rmrGG7lqOsweKzXNeFLv4CQFgxfHwoId9+qE7HbYt0+nCcs7NoxUXHqbsGQPqLWgDYYnloeoFykh1pQEiO8PdjEGOAhZaN1jkLM5089yQrGce9KVYslPdjSb7/3g6CbS+N/EHVEVxL7LKzTeigW6AJ4dMSe2YA0CVgZ7s3zlqHdXl4xzetj2tWhRdtS6hhXhKzwzMY3AxbZV3had2d08kGIJKsfBXJkCGsZXl67Wl2R0lPDEc3mNVpP9NGR5wrz2l0PDV3aBrpBCuM7mtQNAHcht76eIaK/Uukuar6gjM3Yz0b79Z3RVOZmsWjoxQKZ4KLj8ZeTqnKxeINaqosxb8cCs2YYyc2j943T+XpnG6SF4TVL5V4mPLqZRLIAtgn3FTvGZcE285nVeEk="
    );

    public static SkinTexture DARK_GRAY = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=",
        "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="
    );

    public static SkinTexture BLACK_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTc0NTY5MDEsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85ZjcxNTBkM2U1ZGY3ZjIxNzEyODlmM2M1ODYzYzkyYzJjODkwOGMzMmFkZGM4NmVhN2Y2MWVkODQ3YjY5ZCJ9fX0=",
        "v4HSztoVOn0TMBHyDymPBItvOpWs8z6twVamfdrE4yhr8HZoBYWzl+qxfWu+8+DNyBSCpN6nr/UQpFErdys9Kk6urIURx8mEeWDXcYOhXrFs7oNpXiD7UvYm4nd+vNr0xbpfQmSXBGIZ+eOei4ThbKdkSIB79mlq0ugbwxCsK2I8kUlUU6+KnsunPr80adcPu9RryAW00Bmta+eP65nIKwUWNKeLb5iHaPq+N/IZ5aKHmLFiSXiWniDB5UAYybkBZFuvosSr4TBpn1pTbEF3PtKpnPM/8mpt+97W1JcCAv0mdFZUr0hT9eMAe3U0r37J4w/RmLd0sCD7zOBX0pIPPIMrXOQ4DfuDbKSJPXyXiLQQrWCHYnOO8+8kiQcoQ427trsb2y+jMwYel2GEU6gS5zOdkkVm8Je6tNxgA8vRGqA8ABW8SVQz7y5spk2CGiTQQbV3EeJwKcZHXAAplkoSB0p8fRE1fEY0+REoETg5TbguZnONm1+PdW/LdLifL2tGClSz6Nb4D513zZuJaFc/dQ7yagJR0cFkuWVuI59ZnoKqiEW+tMsrhn7QwCwGN6eASHd9seNvnTXsGOyZ0iOWFCay7JWOGSMr8iGiYCd92kn4r+UbCJ+OtokHzxFIoEHr2hRnN6thye27/tpP9Is7cmmmrDAKvecGtYfsrCVmTIw="
    );

    public static SkinTexture DARK_BLUE_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTc1MzgyNDIsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zM2I2NWQ3OGMwYjI5N2RhMzgxYWJlN2JhZmM1ZmE2YzFhZWVhZTY5NDcxZDlhN2JhYWQxZDQ5Y2FmZjVkYjkifX19",
        "mKqD4+2/uXiIjNmtQDcaGoxghxN3d+YFX5VgVHO1whwWSdEzRtNj3tw5CJOyBZVzQh6GfYqqi38ylfCiBq6t3JAYfaf4BxsDSKyq4yRfNb2JnYQoXGrhIhwMqEEyieXoJUshnBXpneASDf0a+BE5hN29l2vqQGs9VPKggqbt2zpZHyUi6tQMlwOiL+jDKh1bcSr4Wl1Ad8JC/BhUXytOR5u0FWm4vOEsqq8Td7S4Jm+XzgwxogtrpeVhRMFwYL7snjehYF3qh1W9p8PNmJGtZ9AApUctc/qEVnWeCOnDAaKgqozj4ruc4Xk2KMXxj0GmDS6b65C3m71MG+cW/0sS/Rx8vmif9SJum+E8C0X8inNr1nui+3ulmwqC3x0MeVsp81cr+LJIA2dcryKVWV3W6hkYuc3OBUjxrN6NOP495fEsPTFx33eop+pajS8g8Re4drNYHUSy7suzDOq+Yv1XM3hOASN/msCU79/tj1G7HIQ2+RHz2eFZlUWC3IEX+5BM5cf7uK1hhkkbejUkmQSblFGeuTnUdV7Yr6kmn0kXQbHAOIrRvw8d91x1lHoX7yjh+HbG4+RymOcm09MJAfdaut7Q8fINHM7rsiHOT6P0Tj7u1UXUUT9w9YXkVgEPCpjSjsjfkGblq/bzDzg8d/es3aZd00WmqFGjlNMx/EKVf/A="
    );

    public static SkinTexture DARK_GREEN_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTc2MzUyNTgsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xMDU3ZTUxYWE2MmFmMjQ4NjdjNjc1Mzc4NmYyMzRkOWJlYjI1NDEyMjM5MTc4YTcxODZkODE4NjkxNmU2YyJ9fX0=",
        "P6gT4sSTDPKon9h+Qp/JEK1minlQQRi0ReIsYdYFzHiVpOejPxq4MU+0Ebmvs23mgX2tgqUQV8oeHK9xNCoTB4cKrLMq0U7JJ4C6ShCrQXL3Mr6MilVIDUbP4Fvj9uLU9vFCEiyHVnYwKlnIsnDxnOOBFzki5TyaFTVt2LeNrlso6eh9ARpa5PCAevtEDDrZ51ftvoI268RrbQTfgE7coiDYAcEzgSY9Lm5I9vQxIN6VWhh/tUxHHdn8u1eDIbga/I/EuQZ6H+V0E3THmR55ob4mKZ4e3TYWWRRlOIZPOdFcPIluVdkVZG03cQfw8RYsON/dazBnbME55NTF8U6ovyDWcwYhCEJtRtA4yGMOocX06InRDOkmFk8FcomtJF0WFSSZRbNfxEmG1XXZx1VtKX4eYR97Y/ihOBilN+Aq5yQ2AB+JvEPb1NFiQHdu3mQ/E4tJxKgsorFd75xbPBV7A/mPsrUV2GHvLFJzlBQGDYJCWqqIEdRV/V417j0XlpM+D7UBiIekcsDo2ajOg0v5mQqGF5d1+H16K58PgAOkPLmKwy3lTAnpPWhSWfcrSbkon93eezlD6eMZie8YG2ucaPR9TXYXRwLxK9DrCx5ZfzQV4lgeDSsL2i8kNnYmTLZ1DnsVw8IwWifUFGwwapHFWO1thdy5yifjaa/AOWYTJQE="
    );

    public static SkinTexture DARK_AQUA_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTc3MDY0NTAsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mODZiZmEyODRjOGM1ODk3NTFiMzQ1NDA1NTc0NGM4YmM0MGVmNjVlYTZiY2RlYzc3NDg2YzllNDE3N2FmOSJ9fX0=",
        "LGisiuVbSaI9K2dXN9CsV8SxuXieWvCHTg1mdjUh744AYQBbINiEgEbXiFz3cQXzzJttUAW8MPZyBnl4qxJr5QRXd4mB8bOd6nA9eimRPiCIpS8QG6V5YZWdyEvKuV+xpdWNkB8I4kpgkRFBmYsKNWeg3xhmgeA0QP1kpadGKU4oOMb5vzUzsIVbyycjhtGcRNuGKjZaNoHVssuTUpi83xIqdCPI353rsltncBY4Idheig0cUzBXqvLX0K3pVxVgg+jQxRPRlGdLhiMXXs6IkRj1glvcZO4MEMg4IfA/W6Vq4UiVG6RpubS53jDJDt4G4ZrtSVX07rQOowHlnY8xIgtpSAbW2iy+uHu3mvv4lWATZE7irU3QrRg7PUSZYSIihqY+CWp8eJwhs+JxTTBMqCzfv5HvZg73WVj2003JTIIxtqTyrlBxIa+4DnPBhoCJ+N0v7wmLdZQC+PsVaRAC8nrlhgDewfglW9HHhqU8m5LVa5gDyMlkIOo6w8cW3mOCTqndGNf4GmDVCbli5Yu5SptaZXvibCp8HcNmkJdP6yf4h/hKPEvOc1QzQOm4L5+9jJxGtpZA2+sTDXqucI2TPy5XNxd9+GaZP9J/xMiwihqeGYmNXbjCJdFSE5NX71HH0NbIVQu5G2VIFccuKkz0iDP1vl60ZiHZdVhn3DcL1gY="
    );

    public static SkinTexture DARK_RED_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTY3MTU0NzcsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82YWM4ZDlmNjQ1ZDU1MDE4MzRkZGZjNGY4YjVkMzIxNmQ5NGYyMTEyOWE2OWQ4YmE0YzJiZjE4YzE0MzEyOWQifX19",
        "D7ub7tuNpCdQckdl9tMJMoSF9XfEAhL0UA4hqMNUcm++uHJviVXbI9ePlg98+N5aisqzVj19/Ld1DaJENFH8eKtQ+lhvzS6Rh+x/JF/QGfDGbWSbx9J9H86LvJpY28hQ/XJ1cFSmtqK5xlnXBNilFj+sAtqCJ+KRdPzwnhgvMNqFMmy5op4qjHaQH4QN997DDF8RWt/9dMzoBDS32sr9RAwzyQzuRsa2mHyMbrrPzST78tcyBWk/dddE5zY9qdt7sDtkar3hDOEMgrid+ChxmAlh/LulaXhpW2S0XEsUEY3uBc58iNrnIWaQedaYYaFFdXPe95BPMaMXtgHblRsOhWlvTAXv9P/CB3v0PKlbfH+kFZyf/OgHfkZan575dAdeEl5mVdT6h7rqriau3MIj/MPUX76xMY+0cbNUGG8Dmw5s6Fw2CnkBuRJWaf9NlSynxm0S/K8BqF/UdGCHc3BVYJ54Bhc65KyN2EONaON2OT2p41Ssvt0Z1UEdb9w+0G+pMYJ9qp/firkdsyQC0VQepymxTDvsmhrA+MC/fb4QL47XucJIFGzhi/qbxj+AjKSt6jhbqYUNE74Y4S8U6fsjIko5dEiQ1eVhq+TOsGAiF0M5M84Jpcyh3B/agvJLic4zIwR0AEE91Ok5ZM8hOimhQf206XvYItw3FqNGjdOsiPw="
    );

    public static SkinTexture DARK_PURPLE_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTc5MTAxMDMsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83NmNiYjYzYWFjMzM2OWE2NzI1ZDQyZmEyMjM4OGFhZmUzYzFiM2YyYTE1MTlkMTA3MjE4NDMxOTgzNWNjYyJ9fX0=",
        "QUWtd7snPe+R9CWdOgAVlagJE79wO4NUGouqctu+/38p2L2b8PRyFydPURaN7gimsBd3+XM6fd2SqS5goc/wj/YXn7FKrIAWP7Wlq1+638006wKaqtyeIXPIOEGBt5fZ/ooYvKDlVjEkyZ1MHwhUVtkmBDUGkvG05WKOJ0LscyCuUw93qfE9LJjowMLubIw1Aq3gSo0dmcN+KcSebHxP7ppThE14BZBrUr2h5zbu1LLYSDKgjFiO3BFkVKFTXtz8P3/kLprQUycT+ojGf8aye2WjO9GlEkiEEj/MLb31B8ImbOZcFWpmqEGfxctKyK2UNZdVrof+Y2qKpZMMVcn6H1SWCM+H/vt7y0wxs9j6kk+xkFTgQJUJY4Y+lBuT+id5Zm0iP3Ua/dCauhlQNezWvCnvx/ICKtGaibzpsouScj/2XMapnQQBNnurTK6v5viDvlt3vF5sdg/pRYDtqyKF6j0prBjQJBayAmANMIefAVYKwGyHONg+JJi72JEm9srIamp1a3ijfP+gYj/1wKtu38w4t3Yfbr7PRO5ArhZbUKvxw+nIDRPwK41SqARU/j9aWVtsud4ASigNno8KNndH9Q/RIfXXukKBNi69PRvPgG+FKhrpN+U+13Zcdzx0mtkMK+ZGb1Qp05Ko7gDfCFawWQE8wKiFC6mIWzWVSeCMqdQ="
    );

    public static SkinTexture GOLD_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTc5NDUxMzEsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jOGM0ZDQ0OGIxZTEzYTI1ZGUzODg0ZGIxN2Q5YjBlZDM4ZGY1ZDEyM2RlNTc3M2YzODIyZWJiNTNhNzYxZTMzIn19fQ==",
        "gdr3b0Zy/uOlU4VdDuAmnPGhKlg9qImK4zj0qoisJZfiVYrPNPeQCocxVvnkAYqsuTsoe7UUo5/oW/G6Z6AKw+2aapkNbUSwxhdCb2vLmnIt8WGhxTxKcd2OEdCnAmCNgtcF8kF062yK8Enoni2eJI2oV+7MektoFWV5pWBkSmhNMBuw5AraYv9S0+zJTYjX0eANTuNXV+VKnfzMCcKuyOBwqXhNMzL9vmvXTMBJAr9bYx/xH3POO5xhGRrW4NyuWSE9SXhV4NngSR+59kImfZmSA6d9kuK26feZrfRrAME/UV2rjbnT4WWumYzvrroZKJBcq++yBEsVluEagkWzs8UXOtOiNYttp4ETg19aYObXdQSLGFRzTeVCVw4cHVSX6Svbiie/Kyr+s/5fQX7/LCs7uVlclPMrabQiam/DzDRre3hbEHXTfiUCLFQLjyqOQ1+gsPqWN2E/HMj0I9gbKL6qrgRVstvTf97UXqXxXudbOC3EthdgAM4n/lR8s6RqmqwzfdkWAyvYGW2c49tImnEtaltwhzeprURNy/dEbLkU3KYfXx2nVHO7+d67WJscwHiffyxDpLwTWkclIrC7bl2SKyfib1cElDqzXNzKYeqZ595PkiHeBBRjL9CWLTlXcLMWuJtdqvquCXt6oJ7EtalcVhKE6DHXdtBDDYDaWpA="
    );

    public static SkinTexture GRAY_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTc5NjU3ODgsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yYzkzZWJjNzc3YTg3OWVkZWViMTFiOTY2MWNmYTZkNzdkZjk0ZTY1NWM0ZTI4N2Y5NmY0ZmY3OTI1Mjc2In19fQ==",
        "jL/5arN/cnJbD/DZG9HTo1ckkwZk+XXYUe6QUqbZ0QAm/wecw/mDZqvGwqwnngsgyg0UelxPHvRK3J3z3SdVq7Kavq2UdGkFLr1fe+34bxTnzricHXs4baYDQPOunmbdHgDs+7wwjYJbuQ4nNRE0vQsCmMAe2epchFEDRsiWYuBS0bWCDtQzCBJasGridmH8MIpBmicKQ8OWor9q9T/Pvxh1KEJdpFJuvEGS6xA1J3GxuRT6Iq1elATJNhW3P0MeHU5f/C8N42clLy9K2W8DGm0VuDQbYZmBCaxYcIo3kbkAVVZ6LGHfO8q0kxKIq9q+qXcmn5TQK3BmyjqTGY3PFYRVR9thgA+LWk1s1Ln1jA6ousckJmQJa1eW73mRt91fvRVAMUGm5qrwScL6q2pe3BjBfB3OnilSR3x2cdwM3WkmfN8VuELykJvxHIhDXGCxI5Xg4ghPyZvqW1NNTJmCUokp/fEt+64ZkXoqXzzV88pGZXuVC3ySgg4hdhL4he0tUOVQHttls+lQ2id6R3XaHdh4Hlk/EjSDM5O9auenvKdKAis+vVUV2oqJ+XGb5juD/MOG/INieNfuhQArWeTxyeufsOj8llWEcyYVRVcmJJfjrAagSLuY9OfoNVlSBM2wP92tzQUXQrvHM+mqw9gDlhudOnJAyP6iP8bVsqPN1bU="
    );

    public static SkinTexture DARK_GRAY_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTgyODg3MzcsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lODQzY2ZjM2ExMmNiNjQ0MWY5NjA5MDIwNjM2NzI2NTdlMDhjNmQ4MzQ0YjUyOGE4NDU5YmQ1MTYyYjJkNDgifX19",
        "nPEbxncNxOm4I6BEsRQ+nsSy5qXKsvKkKLTYHUkze9fQS5JYyiTgAuN/ouljx8fBSbEBdFWxFZq68WxF5h3QDIX7O9x2OFtx9L7vURndZ9pACBRJehy2Kt50eLlEeNMfg1z12J0ODQ36fKbujUYK4ad2ZkM+IOd+QGRV2EDRqFpKC4NMIiXnQ40RYo5GJBAIuW96kioCvaN7+jbpyKW7ub9RAj7dVlXeBbP7cCYvPHb74Ww/lw/EhcsbrwH92eqrApr/oq0Aa9MoW4FKLsEejXvYFjzn6d44uPddwjkcWnd0B2Z8Z8PU/ajS+ZYQY/RqQKRo72bjjPKmn92A19ULtPTFTSOsxxVmLTSSFbYqujt4hUd7BEcDlFcowfhdXtUQWYp1c0DV63UU+dkp8bmnaSCHoG+goyJJN43eqZwSiln2UzEGb+it/wSE3kgsy8q2X4pNgx67ZVUW1ZdRomsBIO0WoanuYoeCNXKRzwMj2mygocdlSer5ZmsnvqG8e+zt8j4iytYDRZ9AWu4euHPPYPbWozAUUbqzsAkogcqa29Bz0mEbr56Per+4806tmPrg+kheI/pBvqCv+HBMRM5ZEEO9FWkTKl2hvr6t6uTNuXFnlVkwspEJaocE+8JJ0E4WDW2mR997uvYYbohY/HPMUG6cK05SwghM9OhQmD8L1Ao="
    );

    public static SkinTexture BLUE_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTYxNzkxNDYsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80ZjE1NWRiMmIwZjE1YmIxMThjMmNjZjYyMWI5Y2JlNDBiZjQzODk4ZmZkODRhZjczMmMxNWYzZGNlNWM4In19fQ==",
        "tXZ/NzLISCygr4aGFLVwW6g8lZdoPruN472pVGHepHU+e+spVjOoQQ/vXrIl2nO5EZe4yVsXYDGZK7UD8v3pNfjyXdwAdaRplgQy4B71dvpxoookgN6ytbkr3EV0WucsNCpJPVEAWN8P1DGYVU0lVWyqdphfmbqGaTQOtJonS95TIbfJvmropA86Uc3s6mCOGceb6+wu216IGOtQ1tnBI7wJJF6p3nYm2hXKPDz/ulZNUb6A17T532hfLNRk7tWaPsjzYnKdTFvpVzuVyulfUPz0cxfjQPAreU072/GxKxk2eTlK6qBCT4hOBa+jlnBUUuG8JIL1/HetF5e+svs4LcMzVh1ZSnPsu6ucEpN8VESDK6ErUSuxIm/f2FnaR8eHaqdJBd/1xaA2jAILNP/Y//4G72BdnMnFsv+rVptE+V+yWzg0Pvlv3I0FqGNc+fZWmP4pqjIUd2T7LjtztV7MyBDECg2ASXRhmaaldjKcZAjgaOyPjYkQ8ydFgPMrQB/xR4yiHnivguJWG9ReuwALI480ZKw5VM4evp6JhvYGBkgnrvC+AKGX+gAZhfAnIT+KdUXAJh3LYfy984e1OQQqNCOg+lHR1aPU1RbtSEN878gqYNbMVbZ7Q5fAEo7kz9V9uqmHSrqQ8w4ltUO9W4zbMYJDVVdVAWbzF915ohYpgp8="
    );

    public static SkinTexture GREEN_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTgzMjE5NTAsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMGQzODkzMjYyMWI5NTIzZTlkODdjODcxMzEyNTk3ZTU0Y2QyMjRjODQ0NmY2NTE0ODY1ODE0Y2RlMzhhMTIifX19",
        "huZ5VsNJVXyj1jijC2KE3rE/XbF47jfj/dApSKRqMbTmHQhi3AEFUAetgN0TsBJWaynz/bgADVTY84WsDeeZyW6u1FZHQtOyvq6zxVCw2L1tFrjO7Ts0AYXpNvaZawz+r9OuM01Y62z9oK4VwA7e9oFHDSuo4mExcTgd35cqyxJmqNA2k5xMh88BBiKumJNTenzTEqfQaaWesSmRJWnxZ09zZKhZb2E0m4ekymZPKZWPuxxfOenaFWlpyltLnx/2pC7VRkG1v+zoBe/VmfsEtu5qWPPS8UPtg1Fpx+Q3GtxIApGj0Ni/DiKkaOmOY/5HH9uYD4BtJ3NjXEFCWbkQOXEgLrgwdstRVL53opC3+07QZTbnXVNA2Ua76Gu6T8j+KMxpA0+q0nS+FQZCL2TEt2Pm9nsAZx43kvQ/iA3hnM1hZ64jSQ4nj38mUJ7bnmqM2bcZpQMtIDzwMwswMLh9/jpYFZBK9p5tG2TW91RjSne6R0sOyzZyfOBX/T8oNohBCSVokD4+8SGyBGzknUb1VE0YOZ5HOj3N7agGxhWyPB6DrYCUT8hljtezFO+iPBSBLVo2yuX1PMrrQYB9ir+rTc7mjOYWmL6ENrkSN52fBDCd4yLhPZyU0AP4ov+Nl175c32e5f8Ihhz7IRnVhNDzOE12WS55ynn1IofblMSgfPU="
    );

    public static SkinTexture AQUA_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTgzNDc3NjIsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83YWQzYTk4NmU4N2E3ZDZjMzk5MTNjNmM5MmE1MmNkNTI0MmUyNmU2ZDc4NzRhYzVkYmFiMDdkODNiYzE4NCJ9fX0=",
        "QwTBMrsbG4EGg5GnaYGyWy5S6G7UWJTTafe/22OMDEsmIy2mcaPzTRHdxQjeWPa8Zk5wGlp7ehZMbC262DRdDG5Wg7SKuAkxNdow/u8BhrUJg4Z9nQhfOauoN2tk/F1j+isV+xVi58EsEhhsr9h/1s9vxJKI2GBJJUmTjZ8xzYmcsihpoM4LaHKRu8sXWQ1MHa5QBgqzOy8UPRabFFNg/5GT8xo0UqM/5FlsYMdiIPsEdOi5bMPsU6ZTco/hWpqTMAlhDMw3hLUxvknnQ6Pf1Mi6GOqTSucaKggAyrj++p9LMtihwBOvRHhWUfIPTKdZ05JllS8Q6aZioOqVbsd+GLHYZp/PKtwKNC62rPzpqZMIBw3HgZ5ciQhSmDMr/l2dDUKEn/LIAJew9P+GXVEpPTAJSLy2IkcEKzrtU5ZSTmbZwzzldviK0+tPVCE3AzMcnYqoVbj4uOpo9Uo/uaunLWm8/PWcjRw3qy8c3czxC1xVynEDA5J6wjo8oqjSnEOuHuJ6cFLCYUf4QMlRmVIaA4lllAOOr+68QEHzek+DRc6rNgkUoqCcHF53YCfH7JItpSxxTZ6snFS2LI20KFUp93DibVEkxifkyQLbqB1OAcyDXkji8HJ7IKUMAUKoNFpLL5eANJsv/96P81N16rGA3THLe5iz17L5xVX/hpruKAo="
    );

    public static SkinTexture RED_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTgzNjk3OTUsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84OGNiOTNlOTgwZjYyOTMxMzYwNGNkMzMzODEzOTRjNTA5ZjljMTIxOGRjMjYyM2Q2NTBlZmI4ZjNkMjAifX19",
        "FqA2ErdpdSPLYrb+O0O1i5dxEr9ZD7ZhaeiSZmU8QSf6+yA8eGo+KZo0N7EHC6YFXnxkgySFqJYk7itcHQ5bLRKpKCaSKAXP4XX5a94/7x6lNa8ev82L+Er3A/mH/0cllxmngqLY5vK8Hej09NoMMNNAhtp+f07TkqLPCi3J6G4mFK4zHAId1gEIEyj7JxlTDeOeU0MRUE4bbngyt2h5VsGpitFaYWJZTSnl8XsXr+6/diXk1nM9smKmmwIv8C02Ufdw3N4/fB93qWfuDUmpd4RLck6XJpp8qJ95twQTmdlpEkUtTt258mHnCV/pjMY9T0J7R1MDbYajkfhDQ1xIdKpVZEsYroEVv4jNOedbRHccF5XZLG7QcYGUk22A53XT0/zuyIBfQbYk/XShABdIHoNW+PNscrtahAmqqGmxkxQqZdI7DdB6V4f5J2YpLkt6k/nXvV1uA+Gm2uPylPvvJusVJJD0Z+BroyM4SsXFfaN9oAqGmNFMdIWR+17vl9TZmfN+K3KodMeiSTcVNUFKVAtMFXSaz80UzmKAB1D+EsZQrJ/Qzo4+OP52TwMmlLOBUY3TvgZkJrK+2MC5kGnwJlW3atjR8JlsKTdNJx/xoCg8HRbaqdxQAfz7zYBRnYfFeuf9Uvb0G7RW33CO0ifqP0gi9HUoQH3LxAiJ0zNqh+o="
    );

    public static SkinTexture LIGHT_PURPLE_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTgzODM4OTUsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lN2E3ZmY2M2RjZDU4NWM0YzZmNjM4OWI0ODZlMzBkNjg0MjE2NGZjNDRlOTQ2N2I4OWM0ZTFjZTZjMzg2OWY5In19fQ==",
        "HMmoTUZECnTE/LoW4y4+yH7YENNOHqFdhYPFmULfFsEYjU/cY0eUu2LmlFcfjox0RTHqGebR0OmGnfVgim/p6n1RQtOXW0dt3GZKgrU10LPGRvEwQ6SMAB9/o5qKL+tnNDAtzbgrUJQh6kQGI7EU89h1KWeUpXmksPhnAPTvuXhFYEj467Ocg5Jz+CT1n8x4Gu0lXI8v8jHW+G/KP2JSQkLeLkqVRb8dGs9laFRvVwXQ3zLgTFgksgwGnuSlWTxoKVS//oLjwlZtiSowg1g+mQJJZxoH8esfAFGsxNKV+7JrdN13PfCkbdZYDpm5eom4SdrSflP3DXCBuGvBSFoStDYYegZEY5t7GhRizubZOU8ze3hfnbU8HqXZH4wqvmHzH7PhM6edQUrgJ+j9HV5jfpV9fQ9vhtlAFg9x1s9V8+yxpQ827FIqwyR4LY5cVZHyQCl1vkeCSuImrIUmYZ3ZsbQXzODDlzLEox++YDiEnUeuRCdJi4MInO9oKbZnZltEO4fsyWj7dRgQZQKWCR+iXaWiGsNmiiJiishn2+dEjFGg0L0pG6KE4XmkCJtDX2VvvgK3q4e9a0mZucZn68SjV3CcBghXkZ31pKzxxaQrKQoxs1KZFfG2UL7QDGmUURZxwWYzOo6KhI9eyAx41mujy8xlzarQzUErN4cwoFoPSmA="
    );

    public static SkinTexture YELLOW_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTg0MDE0NzAsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zYzY2ZDFmMDhkZmRmZTY2ZjljZDExYjA1MmY0YzM1YTdkMjk0MWZmMjNhOTRjY2UyM2Y3ZjgyMTg1NzcyZDgifX19",
        "Rs2GuNrHjFBLk09ymZ/3UpTNSbbjmnZ3WvzA8n0X0gwzNHAx+8u/pNWO2uMEW1TZE5fhmrRAb8krfEpr/D5RdeXl+Se7NRch4mxpWqz4jiipMigFgGnf/s0JY+/dn5amGnoHKzqsktHFx3qwwOjGaz2jj1vhysuzQLGptnurn9wnRgVIueWfR2Cctc0v1pJ9jBVx/gkG3N+2Wznml+50pphxhcYBtfUKtwnMRxHIOz0me1KuRhqmBtmMzoQUArJXiz7cAX8cRlTqUg2ilY4UYLxNsH3cyaJi//tOzpk7EEwo2W1vYT/ZqiHTUvDBeRSu4Or9YZ7TwF/klbSnZJqaC2X0du00QcaoGAvFPY+A9HXZ3QII7k4g0M+aI3huiDUQX24O3p9h4J4dKJpktq1FH0G271uf5m3DMQlAQHTJWP07r3rL/23hAALMtIjE4SvUANd+WBxAeAlgSQamWk2YKQv/TxnNlr6tZMKOz/L1xsXJdn4eATWO786wH5IlxHKwgIQnmEYPSZX3AYsGtIsYuQhttbjmqYiecNdywXy6/WpZwhUHWW2aKuvkczJ8kX5Mcq+viVQRWRJNsCwqwzeXMLEX5tpNGOlQAyRJ/lyGUYJKmEXyqWXIxJstbJG09OR/G9V53ZhNa1hEaaVGv1FwZ9WdH1xHhGyJySz9JtrmKQ0="
    );

    public static SkinTexture WHITE_DOT = new SkinTexture(
        "eyJ0aW1lc3RhbXAiOjE0NTI0MTg0MjUyNTQsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMWViNWQ1MzExZjI1YjQ5MmEyMTIyYWM0YTNkYWUzZjNiMmRmNTc1NmFjYWM4ZjVjYjk2NWEyYjhjMGY4In19fQ==",
        "LHeNKZWS3u7hmMmxqjEiCLAlhHRR4ZzQDZkCwJNid0odX38Qm98teyCwg4VmYKUrovIpqX3xReYiZ2LIY2SQDz6nARj6qarHQHDMgZ6Itqfd8jue5ZlzbwRwv9Fmirxdq67yA/VAMmy7Hel60X39PF/qVlVmA1k9nFz2NDmMlASA61nI2oEjfvdwRgODAG0rSkocIqxpZ8y/hUAsUsP2NPISIRl+yY+QLpzkx56+iTvLvYsYbhFMMJyjshjgL6j/TH9XRyjfqxfthTiKrH7zYSbxIb1nQC+Osrzg2EN9M2BPfyvF/MiFsQGu8It9CXSSR6ZFqTnDmhteFySiOrC8WF6F6rZL+vMYLSgke4vixLFLLdgdb1NBBMy8wGfEJFfLGs0n7UcnHERLg8ZTzz3yov6vKVd5dqv8uClQGHbv4iHVpvvepZ8BUuPH4PQgxqhs1akQ/q9B0RVXucigEcQMWfBAftGEDUI9PL17jjjsNbLYnX7yjSV3AWi3PPFVs7JWXrG+9KQPYHO1OuoA0ld3gA50+nSRXqcpDrvxRqo88MlqAv54Wc/I/lYOfpzx9BCgQsMz7n6wq22BsGLhNdbB+Usw/GB8s50KDZ91Zigc2REljgyoabzNBMHa/ACaPiBuFZ8ApBd84no+ipnpVJXnNcFxSH44AShuIcZaCdBlwbw="
    );

    /**
     * Get the skin data by a player's unique identifier
     *
     * @param uuid the unique identifier to get the skin data by
     * @return the skin data
     * @throws IOException if you find any bugs in resolving this
     */
    public static SkinTexture getSkinData(UUID uuid) throws IOException {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }

        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false");
        try {
            JsonObject json = new JsonParser().parse(new InputStreamReader(url.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            return cache.put(uuid, new SkinTexture(
                    json.get("value").getAsString(),
                    json.get("signature").getAsString()));
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
