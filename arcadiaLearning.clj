(use 'arcadia.core)
(use 'arcadia.introspection)
(use 'clojure.repl)
(dir arcadia.core)
(dir arcadia.introspection)

(source create-primitive)
(doc create-primitive)
(create-primitive :capsule)
(def capsule (object-named "Capsule"))
(source set-state!)
(source state)
(state capsule)

(doc ..)
(.. capsule transform position)
(import UnityEngine.Vector3)
(set! 
	(.. capsule transform position) 
	(Vector3. 0 0 0))



(import UnityEngine.Screen)
(set! (.. Screen fullScreen) true)

(def TWO_PI (* 2 (.. Mathf PI)))

(defn cube [position]
	(let [c (create-primitive :cube)]
		(set! 
			(.. c transform position)
			position)
		c))

(def ccc (cube (Vector3. 0 5 0)))

(import UnityEngine.Mathf)
(.. Mathf (Sin 1.0))

(defn ring-of-cubes [n r]
	(->> (range n)
	 (map (fn[x] (cube (Vector3. 
	 	(* r (.. Mathf (Cos (* (/ TWO_PI n) x)))) 
	 	0
	 	(* r (.. Mathf (Sin (* (/ TWO_PI n) x))))))))))

(ring-of-cubes 12 6)


(destroy (object-named "Cube"))

(->> (objects-named "Cube")
	(map (fn[c] (destroy c))))


(def cubey (create-primitive :cube))
(set! 
	(.. cubey transform position) 
	(Vector3. 0 3 5))

(->> (objects-named "Cube")
	(map (fn[c] 
		(.. c transform (SetParent (.. cubey transform))))))


(.. cubey transform (Rotate 0 10 0))

(doc hook+)
(hook+ cubey :update (fn[go] (.. go transform (Rotate 0 0.1 0))))


(require '[clojure.reflect :as r])
(use '[clojure.pprint :only [print-table]])
(defn all-methods [x]
    (->> x r/reflect 
           :members 
           (filter :return-type)  
           (map :name) 
           sort 
           (map #(str "." %) )
           distinct
           println))
(all-methods UnityEngine.Vector3)

(r/reflect UnityEngine.Vector3)

(doc print-table)

(print-table
  (sort-by :name 
    (:members (r/reflect UnityEngine.Vector3))))


; Procedural Geometry
(import UnityEngine.GameObject)
(import UnityEngine.Mesh)
(import UnityEngine.MeshFilter)
(import UnityEngine.MeshRenderer)




(def mesh (Mesh.))
(def go (GameObject.))
(def mf (cmpt+ go MeshFilter))
(def mr (cmpt+ go MeshRenderer))
(set! (.. mf mesh) mesh)

(import UnityEngine.Mathf)
(import UnityEngine.Vector2)
(import UnityEngine.Vector3)
(def TWO_PI (* 2 (.. Mathf PI)))

(defn polygon-verts-2d 
	"returns 2d vectors of a unit polygon with n sides" 
	[sides]
	(->> 
		(range sides)
		(map (fn [n] [
			(.. Mathf (Cos (* (/ TWO_PI sides) n)))
		 	(.. Mathf (Sin (* (/ TWO_PI sides) n)))
	 	]))
	 	(cons [0 0])
	 	(reverse)))

(defn polygon-verts [sides]
	 	(map 
	 		(fn [[x y]] (Vector3. x 0 y))
	 		(polygon-verts-2d sides)))

(defn polygon-uvs [sides]
	 	(map 
	 		(fn [[x y]] (Vector2. x y))
	 		(polygon-verts-2d sides)))

(defn polygon-tris [sides]
	(->>
		(range sides)
		(map (fn [n] [ 
			n 
			(mod (+ 1 n) sides) 
			sides
		]))
		flatten))

(defn generate-polygon-mesh [mesh sides]
	(.. mesh (Clear))
	(set! (.. mesh vertices) (into-array Vector3 (polygon-verts sides)))
	(set! (.. mesh uv) (into-array Vector2 (polygon-uvs sides)))
	(set! (.. mesh triangles) (into-array Int32 (polygon-tris sides))))

(defn polygon 
	"returns a GameObject containing a unit polygon with n sides"
	[sides]
	(let [
			mesh (Mesh.)
			go (GameObject.)
			mf (cmpt+ go MeshFilter)
			mr (cmpt+ go MeshRenderer)
		]
		(set! (.. go name) (str "poly-" sides))
		(set! (.. mf mesh) mesh)
		(generate-polygon-mesh mesh sides)
		go))

(polygon 6)
(polygon 3)
(polygon 8)

(dir arcadia.core)
(doc update-state!)

(def cam (object-named "Main Camera"))
(members cam)
(set! (.. cam transform position) (Vector3. 0 2 -3))
(.. cam transform (Rotate 10 0 0))

(type (into-array (polygon-tris 3)))
(set! (.. mesh vertices) (vec (polygon-verts 3)))

((fn [[a b]] (str a b)) [3 4])

(properties Mesh)


(dir arcadia.core)
(doc cmpts)



(doc cmpt+)
(doc gobj)
(source create-primitive)
