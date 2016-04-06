# AsyncTask
C# style async, await

# simple
## Method
```kotlin
    fun runOnAsync(): Task<String> {
        return async() {
            Thread.sleep(2000)
            "OnAsync"
        }

    }

```
## call async
```kotlin
    val task = runOnAsync()
    task.result { println("async result = $it") }
```

## call await
```kotlin
    val result = await{ runOnAsync() }
    println("await result = $result")
```

# 備註

>如果呼叫 async function，將會馬上執行。
>結果會在設定result後回傳(function執行完畢後)。
>如果設定result前function已經執行完畢，
>結果會在設定result後立刻回傳。
>async function內部發生Exception後，
>不會拋出Exception，而是在result回傳null。

>使用await後會進行堵塞，等待執行完畢後直接return結果。
>await後發生Exception會直接在執行的Thread上拋出Exception。


# License
```
Copyright 2015 tatsuyuki

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```