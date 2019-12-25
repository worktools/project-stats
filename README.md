
Project Stats(WIP)
----

> scripts to gather file informations.

### Usage

```bash
yarn global add @jimengio/project-stats
```

### `unimported`

`unimported` is a command for listing files not required from entry file:

```bash
project-unimported src/main.tsx
```

it relies on `package.json` and `tsconfig.json` for configs.

### Stats(TODO)

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
