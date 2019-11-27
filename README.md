
Project Stats(WIP)
----

> scripts to gather file informations.

### Usage

TODO.

```bash
project-stats config.edn
```

```edn
{
  :base "/home/jimeng/repo/web/"
  :pick-entries ["shared" "system"]
  :highlights [
    {:type :lines, :succeed 800}
    {:type :frequency, :content "useState", :succeed 10}
  ]
}
```

### Workflow

Workflow https://github.com/mvc-works/calcit-workflow

### License

MIT
