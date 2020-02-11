
Project Stats(WIP)
----

> scripts to gather file informations.

### Usage

[![npm](https://img.shields.io/npm/v/@jimengio/project-stats)](https://www.npmjs.com/package/@jimengio/project-stats)

```bash
yarn global add @jimengio/project-stats
```

### `unimported`

`unimported` is a command for listing files not required from entry file:

```bash
project-unimported src/main.tsx
```

it relies on `package.json` and `tsconfig.json` for configs.

### `uppercase`

List all files with uppercase letter in filename:

```bash
project-uppercase src/
```

### `frequency`

Find editing frequency of each file from Git history:

```bash
project-frequency

project-frequency src # paths begin with "src"
```

### `usages`

Detect usages of `useState`(currently only useState):

```bash
project-usages
```

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
