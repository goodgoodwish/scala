## A concise demo for State Monad.

You may follow this pattern.

```scala
  def stackManipulation: State[Stack, Int] = for {
    _ <- push(3)
    a <- pop
    b <- pop
  } yield(b) 
```

## upload code to github

```
echo "# StateMonadDemo1" >> README.md
git init
git add .
git commit -m "first commit"
git remote add origin https://github.com/goodgoodwish/StateMonadDemo1.git
git push -u origin master


# More code changes, and push.

git add .
git commit -m "first commit"
git push -u origin master

```
