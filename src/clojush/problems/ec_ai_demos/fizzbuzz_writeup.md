# FizzBuzz Writeup
#### Ryan McArthur, Laverne Schrock, and Jack Ziegler

### Problem description
We decided to try evolving a program that can solve the classic FizzBuzz problem. When given an integer we expect our program to return one of four numbers. It should return 3 if the input is divisible by 3 and 5, it should return 2 if only divisible by 5, return 1 if only divisible by 3, and return 0 if not divisible by 3 or 5.

We chose this problem because it seemed like a reasonably straightforward problem, but hard enough to not be solved instantly.

### Setup

Initially, our input data was the range of number from 1-60. However, that data was imbalanced and we later modified it to compensate. Our input that allowed us (well, the computer did the work) to find a solution was the first 20 numbers that met each of our 4 criteria ("Fizz", "Buzz", "FizzBuzz", and none of the three.)

For atom-generators, we initially allowed the system to use all of the `:exec`, `:integer`, and `:boolean` instruction sets. We also provided it with the numbers 3 and 5, as well as a random number generator. When we changed the input data, we also removed the random number generator, and also included extra copies of `5` and `integer_mod` (3 was not duplicated by mistake) which allowed us to solve the problem.

### Our Results

It took a few strategies for us to get a solution. However, we did get there in the end. Clojush found a solution in 99 generations, the full output can be found in out3.txt in the root directory. The simplified program is included below.

```
(2 5 exec_shove 5 exec_s (3 integer_stackdepth)
integer_mod exec_noop in1 integer_mod integer_mod
integer_stackdepth in1 integer_mod exec_yankdup
boolean_or exec_while boolean_pop integer_stackdepth
in1 3 integer_mod integer_mod integer_yank exec_do*count ())
```
