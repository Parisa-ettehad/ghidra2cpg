(module
  (type $alloc_t   (func (result i32)))
  (type $free_t    (func (param i32)))
  (type $write_t   (func (param i32 i32)))
  (type $read_t    (func (param i32) (result i32)))
  (type $void_t    (func))

  (import "env" "malloc"    (func $malloc    (type $alloc_t)))
  (import "env" "free"      (func $free      (type $free_t)))
  (import "env" "write_i32" (func $write_i32 (type $write_t)))
  (import "env" "read_i32"  (func $read_i32  (type $read_t)))

  (func $bad (type $void_t)
    (local $ptr i32)

    ;; allocate
    call $malloc
    local.set $ptr

    ;; normal use
    local.get $ptr
    i32.const 123
    call $write_i32

    ;; free
    local.get $ptr
    call $free

    ;; 🔥 use-after-free
    local.get $ptr
    call $read_i32
    drop
  )

  (export "bad" (func $bad))
)